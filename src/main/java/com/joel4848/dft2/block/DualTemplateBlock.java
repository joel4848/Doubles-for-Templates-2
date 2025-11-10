package com.joel4848.dft2.block;

import com.joel4848.dft2.DFT2;
import com.google.common.base.MoreObjects;
import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for dual-texture template blocks.
 * Subclasses must define two voxel slice shapes.
 */
public abstract class DualTemplateBlock extends Block implements BlockEntityProvider {

    public DualTemplateBlock(Settings settings) {
        super(settings);
        setDefaultState(TemplateInteractionUtil.setDefaultStates(getDefaultState()));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(TemplateInteractionUtil.appendProperties(builder));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return TemplateInteractionUtil.modifyPlacementState(super.getPlacementState(ctx), ctx);
    }

    /**
     * Subclasses must return theme slice shape #1 or #2.
     */
    public abstract VoxelShape getThemeShape(BlockState state, int themeIndex);

    /**
     * Determines which theme slice a hit position corresponds to.
     * Returns 1 for slice 1, 2 for slice 2.
     */
    protected int getHitThemeIndex(BlockState state, Vec3d hit, BlockPos pos) {
        Vec3d local = hit.subtract(pos.getX(), pos.getY(), pos.getZ());
        VoxelShape slice1 = getThemeShape(state, 1);

        // Use bounding boxes to determine which slice contains the hit
        boolean inSlice1 = slice1.getBoundingBoxes().stream().anyMatch(bb ->
                local.x >= bb.minX && local.x <= bb.maxX &&
                        local.y >= bb.minY && local.y <= bb.maxY &&
                        local.z >= bb.minZ && local.z <= bb.maxZ
        );

        return inSlice1 ? 1 : 2;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack held, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // First, try the standard Template interactions (glowstone, redstone, etc.)
        ItemActionResult result = TemplateInteractionUtil.onUseWithItem(held, state, world, pos, player, hand, hit);
        if (result.isAccepted()) return result;

        // If that didn't work, try applying a theme
        if (!(world.getBlockEntity(pos) instanceof DualTemplateEntity be)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!player.canModifyBlocks() || !world.canPlayerModifyAt(player, pos)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Determine which theme slot was clicked
        int themeIndex = getHitThemeIndex(state, hit.getPos(), pos);

        // Check if we're placing a block theme
        if (held.getItem() instanceof BlockItem bi && be.getThemeState(themeIndex).isAir()) {
            net.minecraft.block.Block block = bi.getBlock();
            ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hit));
            BlockState placementState = block.getPlacementState(ctx);

            if (placementState != null &&
                    net.minecraft.block.Block.isShapeFullCube(placementState.getCollisionShape(world, pos)) &&
                    !(block instanceof BlockEntityProvider)) {

                if (!world.isClient) {
                    be.setThemeState(placementState, themeIndex);
                }

                // Update light state if needed (only if LIGHT property exists)
                if (state.contains(TemplateInteractionUtil.LIGHT)) {
                    boolean shouldEmitLight = be.getThemeState(1).getLuminance() != 0 ||
                            be.getThemeState(2).getLuminance() != 0;
                    world.setBlockState(pos, state.with(TemplateInteractionUtil.LIGHT, shouldEmitLight));
                }

                if (!player.isCreative()) held.decrement(1);
                world.playSound(player, pos, placementState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1f, 1.1f);
                return ItemActionResult.success(world.isClient);
            }
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        TemplateInteractionUtil.onStateReplaced(state, world, pos, newState, moved);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TemplateInteractionUtil.onPlaced(world, pos, state, placer, stack);
        super.onPlaced(world, pos, state, placer, stack);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        return MoreObjects.firstNonNull(
                TemplateInteractionUtil.getCollisionShape(state, view, pos, ctx),
                super.getCollisionShape(state, view, pos, ctx)
        );
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return TemplateInteractionUtil.emitsRedstonePower(state);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
        return TemplateInteractionUtil.getWeakRedstonePower(state, view, pos, dir);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
        return TemplateInteractionUtil.getStrongRedstonePower(state, view, pos, dir);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return TemplateInteractionUtil.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return DFT2.DUAL_TEMPLATE_BLOCK_ENTITY.instantiate(pos, state);
    }
}