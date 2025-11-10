package com.joel4848.dft2;

import com.joel4848.dft2.model.UnbakedDualRetexturedModel;
import io.github.cottonmc.templates.api.TemplatesClientApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class DFT2Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DFT2.LOGGER.info("Initializing DFT2 client");

        // Put all dual template blocks on the cutout layer
        BlockRenderLayerMap.INSTANCE.putBlock(DFT2.SLABS_CUBE, RenderLayer.getCutout());

        // Get the Templates API
        TemplatesClientApi api = TemplatesClientApi.getInstance();

        // Register models for slabs_cube in all three orientations
        // Y axis (vertical)
        api.addTemplateModel(
                DFT2.id("slabs_cube_y_special"),
                new UnbakedDualRetexturedModel(
                        Identifier.of("minecraft", "block/slab"),
                        Identifier.of("minecraft", "block/slab_top")
                )
        );

        // Z axis (north-south)
        api.addTemplateModel(
                DFT2.id("slabs_cube_z_special"),
                new UnbakedDualRetexturedModel(
                        DFT2.id("block/slab_z_bottom"),
                        DFT2.id("block/slab_z_top")
                )
        );

        // X axis (east-west)
        api.addTemplateModel(
                DFT2.id("slabs_cube_x_special"),
                new UnbakedDualRetexturedModel(
                        DFT2.id("block/slab_x_bottom"),
                        DFT2.id("block/slab_x_top")
                )
        );

        // Assign item model (defaults to Y axis orientation)
        api.assignItemModel(DFT2.id("slabs_cube_y_special"), DFT2.SLABS_CUBE);

        DFT2.LOGGER.info("DFT2 client initialized successfully!");
    }
}