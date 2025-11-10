package com.joel4848.dft2;

import com.joel4848.dft2.block.DualTemplateEntity;
import com.joel4848.dft2.block.SlabsCubeBlock;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DFT2 implements ModInitializer {
    public static final String MOD_ID = "dft2";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Blocks
    public static SlabsCubeBlock SLABS_CUBE;

    // Block entity type
    public static BlockEntityType<DualTemplateEntity> DUAL_TEMPLATE_BLOCK_ENTITY;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Dual-texture Frames for Templates 2");

        // Register block (no settings passed; the block itself defines them)
        SLABS_CUBE = registerBlock("slabs_cube", new SlabsCubeBlock());

        // Register block entity type using modern vanilla builder
        DUAL_TEMPLATE_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                id("dual_template"),
                BlockEntityType.Builder
                        .create((pos, state) -> new DualTemplateEntity(DUAL_TEMPLATE_BLOCK_ENTITY, pos, state),
                                SLABS_CUBE)
                        .build(null)
        );

        LOGGER.info("Dual-texture Frames initialized successfully!");
    }

    private static <T extends Block> T registerBlock(String name, T block) {
        Identifier id = id(name);
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
