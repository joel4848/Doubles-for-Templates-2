package com.joel4848.dft2.model;

import com.joel4848.dft2.block.DualTemplateEntity;
import io.github.cottonmc.templates.model.RetexturingBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A baked model that combines two RetexturingBakedModels.
 * This is a simple wrapper that delegates quad queries to both models.
 */
public class DualRetexturingBakedModel implements BakedModel {
    private final RetexturingBakedModel firstModel;
    private final RetexturingBakedModel secondModel;

    public DualRetexturingBakedModel(
            BakedModel firstBaked,
            BakedModel secondBaked
    ) {
        // Cast to RetexturingBakedModel
        if (firstBaked instanceof RetexturingBakedModel) {
            this.firstModel = (RetexturingBakedModel) firstBaked;
        } else {
            throw new IllegalArgumentException("First baked model must be a RetexturingBakedModel");
        }

        if (secondBaked instanceof RetexturingBakedModel) {
            this.secondModel = (RetexturingBakedModel) secondBaked;
        } else {
            throw new IllegalArgumentException("Second baked model must be a RetexturingBakedModel");
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        // Combine quads from both models
        List<BakedQuad> quads = new ArrayList<>();
        quads.addAll(firstModel.getQuads(state, face, random));
        quads.addAll(secondModel.getQuads(state, face, random));
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return firstModel.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return firstModel.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return firstModel.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return firstModel.getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return firstModel.getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return firstModel.getOverrides();
    }
}