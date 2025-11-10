package com.joel4848.dft2.block;

import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

/**
 * SlabsCubeBlock splits the block into two halves according to placement orientation.
 * Each half can have its own texture applied.
 */
public class SlabsCubeBlock extends DualTemplateBlock {
    public static final EnumProperty<Direction.Axis> AXIS =
            EnumProperty.of("axis", Direction.Axis.class);

    public SlabsCubeBlock() {
        super(TemplateInteractionUtil.makeSettings());
        setDefaultState(getDefaultState().with(AXIS, Direction.Axis.Y));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(AXIS));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        if (state != null) {
            // Set axis based on which side was clicked
            state = state.with(AXIS, ctx.getSide().getAxis());
        }
        return state;
    }

    @Override
    public VoxelShape getThemeShape(BlockState state, int themeIndex) {
        Direction.Axis axis = state.get(AXIS);

        return switch (axis) {
            case X -> themeIndex == 1
                    ? VoxelShapes.cuboid(0, 0, 0, 0.5, 1, 1)
                    : VoxelShapes.cuboid(0.5, 0, 0, 1, 1, 1);

            case Y -> themeIndex == 1
                    ? VoxelShapes.cuboid(0, 0, 0, 1, 0.5, 1)
                    : VoxelShapes.cuboid(0, 0.5, 0, 1, 1, 1);

            case Z -> themeIndex == 1
                    ? VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.5)
                    : VoxelShapes.cuboid(0, 0, 0.5, 1, 1, 1);
        };
    }
}