package com.joel4848.dft2.block;

import com.joel4848.dft2.DFT2;
import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for dual-texture template blocks.
 * Subclasses must define two voxel slice shapes.
 */
public abstract class DualTemplateBlock extends Block implements BlockEntityProvider {

    public DualTemplateBlock(Settings settings) {
        super(settings);
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
        return slice1.getBoundingBoxes().stream().anyMatch(bb ->
                local.x >= bb.minX() && local.x <= bb.maxX() &&
                        local.y >= bb.minY() && local.y <= bb.maxY() &&
                        local.z >= bb.minZ() && local.z <= bb.maxZ()
        ) ? 1 : 2;
    }

    /**
     * Vanilla outline shape.
     */
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    /**
     * Required by the Templates API: returns how the block should appear
     * when used as a template host.
     */
    public BlockState getAppearance(
            BlockState state,
            BlockView world,
            BlockPos pos,
            @Nullable Direction side,
            @Nullable BlockState sourceState,
            @Nullable BlockPos sourcePos
    ) {
        // Call via instance to match Cotton 1.21.1 API
        TemplateInteractionUtil util = TemplateInteractionUtil.getInstance();
        return util.getAppearance(state, world, pos, side, sourceState, sourcePos);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DualTemplateEntity(DFT2.DUAL_TEMPLATE_BLOCK_ENTITY, pos, state);
    }

    /**
     * Convenience method: gets theme index from a raycast hit result.
     */
    protected int getHitThemeFromRaycast(BlockState state, BlockHitResult hit) {
        return getHitThemeIndex(state, hit.getPos(), hit.getBlockPos());
    }
}
