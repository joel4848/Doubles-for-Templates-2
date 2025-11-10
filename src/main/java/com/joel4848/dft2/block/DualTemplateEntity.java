package com.joel4848.dft2.block;

import io.github.cottonmc.templates.Templates;
import io.github.cottonmc.templates.block.TemplateEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

/**
 * A block entity that can store two separate theme BlockStates.
 * Extends Templates 2's TemplateEntity to add a second texture slot.
 */
public class DualTemplateEntity extends TemplateEntity {
    protected BlockState secondState = Blocks.AIR.getDefaultState();

    public DualTemplateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * Get the theme BlockState for the given index.
     * Index 1 returns the first theme (from parent TemplateEntity).
     * Index 2 returns the second theme (added by this class).
     */
    public BlockState getThemeState(int index) {
        if (index == 2) {
            return secondState;
        }
        // Index 1 or any other value returns the first theme
        return getThemeState();
    }

    /**
     * Set the theme BlockState for the given index.
     */
    public void setThemeState(BlockState state, int index) {
        if (index == 2) {
            if (!Objects.equals(secondState, state)) {
                secondState = state;
                markDirtyAndDispatch();
            }
        } else {
            // Index 1 or any other value sets the first theme
            setRenderedState(state);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        super.readNbt(nbt, wrapperLookup);

        BlockState lastSecondState = secondState;

        if (nbt.contains(BLOCKSTATE_KEY + "2")) {
            secondState = NbtHelper.toBlockState(
                    wrapperLookup.getWrapperOrThrow(net.minecraft.registry.RegistryKeys.BLOCK),
                    nbt.getCompound(BLOCKSTATE_KEY + "2")
            );
        }

        // Force a chunk remesh on the client if the second state has changed
        if (world != null && world.isClient && !Objects.equals(lastSecondState, secondState)) {
            Templates.chunkRerenderProxy.accept(world, pos);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        super.writeNbt(nbt, wrapperLookup);

        if (secondState != Blocks.AIR.getDefaultState()) {
            nbt.put(BLOCKSTATE_KEY + "2", NbtHelper.fromBlockState(secondState));
        }
    }
}