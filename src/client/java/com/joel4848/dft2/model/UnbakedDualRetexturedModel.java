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
 * Unbaked model that creates a DualRetexturingBakedModel
 * using two parent models (first = base, second = accent).
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
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings settings, BakedModel wrappedModel, TemplateAppearanceManager tam) {
        BakedModel firstBaked = super.bake(baker, spriteLookup, settings, wrappedModel, tam);
        BakedModel secondBaked = secondModel.bake(baker, spriteLookup, settings, firstBaked, tam);
        return new DualRetexturingBakedModel(firstBaked, secondBaked);
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return List.of(firstParentId, secondParentId);
    }
}
