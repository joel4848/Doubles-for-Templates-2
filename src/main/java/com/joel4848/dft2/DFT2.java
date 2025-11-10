package com.joel4848.dft2;

import com.joel4848.dft2.block.DualTemplateEntity;
import com.joel4848.dft2.block.SlabsCubeBlock;
import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DFT2 implements ModInitializer {
    public static final String MOD_ID = "dft2";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Blocks
    public static Block SLABS_CUBE;

    // Block Entity Type
    public static BlockEntityType<DualTemplateEntity> DUAL_TEMPLATE_BLOCK_ENTITY;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Dual-texture Frames for Templates 2");

        // Register blocks
        SLABS_CUBE = registerBlock("slabs_cube", new SlabsCubeBlock(
                TemplateInteractionUtil.makeSettings()
                        .sounds(BlockSoundGroup.WOOD)
                        .hardness(0.2f)
        ));

        // Register block entity type
        DUAL_TEMPLATE_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                id("dual_template"),
                FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> new DualTemplateEntity(DUAL_TEMPLATE_BLOCK_ENTITY, pos, state),
                        SLABS_CUBE
                ).build(null)
        );

        LOGGER.info("Dual-texture Frames initialized successfully!");
    }

    private static Block registerBlock(String name, Block block) {
        Identifier id = id(name);
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}