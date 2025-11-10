package com.joel4848.dft2.model;

import io.github.cottonmc.templates.model.TemplateAppearanceManager;
import io.github.cottonmc.templates.model.UnbakedAutoRetexturedModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * An unbaked model that creates a DualRetexturingBakedModel with two separate parent models.
 */
public class UnbakedDualRetexturedModel extends UnbakedAutoRetexturedModel {
    private final Identifier firstParentId;
    private final Identifier secondParentId;
    private final UnbakedAutoRetexturedModel secondModel;

    public UnbakedDualRetexturedModel(Identifier firstParent, Identifier secondParent) {
        super(firstParent);
        this.firstParentId = firstParent;
        this.secondParentId = secondParent;
        this.secondModel = new UnbakedAutoRetexturedModel(secondParent);
    }

    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings modelBakeSettings, BakedModel wrappedModel, TemplateAppearanceManager tam) {
        // Bake the first model using the parent class
        BakedModel firstBaked = super.bake(baker, spriteLookup, modelBakeSettings, wrappedModel, tam);

        // Bake the second parent model
        BakedModel secondWrappedModel = baker.bake(secondParentId, modelBakeSettings);

        // Bake the second model
        BakedModel secondBaked = secondModel.bake(baker, spriteLookup, modelBakeSettings, secondWrappedModel, tam);

        // Create our dual retexturing baked model
        return new DualRetexturingBakedModel(firstBaked, secondBaked);
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return List.of(firstParentId, secondParentId);
    }
}