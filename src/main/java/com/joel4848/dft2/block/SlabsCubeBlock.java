package com.joel4848.dft2.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

/**
 * A dual-texture block that combines two slabs into a cube.
 * One slab texture on one half, another slab texture on the other half.
 */
public class SlabsCubeBlock extends DualTemplateBlock {

    // Slab shapes for each direction
    protected static final VoxelShape DOWN = VoxelShapes.cuboid(0f, 0f, 0f, 1f, 0.5f, 1f);
    protected static final VoxelShape UP = VoxelShapes.cuboid(0f, 0.5f, 0f, 1f, 1f, 1f);
    protected static final VoxelShape NORTH = VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1f, 0.5f);
    protected static final VoxelShape SOUTH = VoxelShapes.cuboid(0f, 0f, 0.5f, 1f, 1f, 1f);
    protected static final VoxelShape EAST = VoxelShapes.cuboid(0.5f, 0f, 0f, 1f, 1f, 1f);
    protected static final VoxelShape WEST = VoxelShapes.cuboid(0f, 0f, 0f, 0.5f, 1f, 1f);

    public SlabsCubeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.AXIS, Direction.Axis.Y));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.AXIS));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        if (state != null) {
            // Set axis based on which side was clicked
            state = state.with(Properties.AXIS, ctx.getSide().getAxis());
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        Direction.Axis axis = state.get(Properties.AXIS);
        Direction dir = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        Direction rotated = rotation.rotate(dir);
        return state.with(Properties.AXIS, rotated.getAxis());
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Direction.Axis axis = state.get(Properties.AXIS);
        Direction dir = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        Direction mirrored = mirror.apply(dir);
        return state.with(Properties.AXIS, mirrored.getAxis());
    }

    @Override
    public VoxelShape getThemeShape(BlockState state, int themeIndex) {
        Direction.Axis axis = state.get(Properties.AXIS);

        // Theme 1 is the "bottom/negative" half
        // Theme 2 is the "top/positive" half
        if (themeIndex == 2) {
            return switch (axis) {
                case Y -> UP;
                case Z -> SOUTH;
                case X -> EAST;
            };
        } else {
            return switch (axis) {
                case Y -> DOWN;
                case Z -> NORTH;
                case X -> WEST;
            };
        }
    }
}