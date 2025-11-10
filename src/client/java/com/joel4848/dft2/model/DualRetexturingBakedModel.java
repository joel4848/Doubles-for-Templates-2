package com.joel4848.dft2.model;

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

public class DualRetexturingBakedModel implements BakedModel {
    private final RetexturingBakedModel firstModel;
    private final RetexturingBakedModel secondModel;

    public DualRetexturingBakedModel(BakedModel firstBaked, BakedModel secondBaked) {
        if (!(firstBaked instanceof RetexturingBakedModel fm) || !(secondBaked instanceof RetexturingBakedModel sm)) {
            throw new IllegalArgumentException("Both baked models must be RetexturingBakedModel");
        }
        this.firstModel = fm;
        this.secondModel = sm;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        List<BakedQuad> quads = new ArrayList<>();
        quads.addAll(firstModel.getQuads(state, face, random));
        quads.addAll(secondModel.getQuads(state, face, random));
        return quads;
    }

    @Override public boolean useAmbientOcclusion() { return firstModel.useAmbientOcclusion(); }
    @Override public boolean hasDepth() { return firstModel.hasDepth(); }
    @Override public boolean isSideLit() { return firstModel.isSideLit(); }
    @Override public boolean isBuiltin() { return false; }
    @Override public Sprite getParticleSprite() { return firstModel.getParticleSprite(); }
    @Override public ModelTransformation getTransformation() { return firstModel.getTransformation(); }
    @Override public ModelOverrideList getOverrides() { return firstModel.getOverrides(); }
}
