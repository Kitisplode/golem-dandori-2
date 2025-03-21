package com.kitisplode.golemdandori2.client.model.entity;

import com.kitisplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class ModelGolem extends GeoModel<AbstractGolemDandoriPik>
{
    @Override
    public ResourceLocation getModelResource(AbstractGolemDandoriPik animatable, @Nullable GeoRenderer<AbstractGolemDandoriPik> renderer)
    {
        return animatable.getModel();
    }

    @Override
    public ResourceLocation getTextureResource(AbstractGolemDandoriPik animatable, @Nullable GeoRenderer<AbstractGolemDandoriPik> renderer)
    {
        return animatable.getTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(AbstractGolemDandoriPik animatable)
    {
        return animatable.getAnimations();
    }

    @Override
    public void setCustomAnimations(AbstractGolemDandoriPik animatable, long instanceId, AnimationState<AbstractGolemDandoriPik> animationState)
    {

    }
}
