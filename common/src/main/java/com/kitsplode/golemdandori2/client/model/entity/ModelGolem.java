package com.kitsplode.golemdandori2.client.model.entity;

import com.kitsplode.golemdandori2.ExampleModCommon;
import com.kitsplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class ModelGolem extends DefaultedEntityGeoModel<AbstractGolemDandoriPik> //GeoModel<AbstractGolemDandoriPik>
{
    public ModelGolem()
    {
        super(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "legends/golem_cobble"));
    }

//    @Override
//    public ResourceLocation getModelResource(AbstractGolemDandoriPik animatable, @Nullable GeoRenderer<AbstractGolemDandoriPik> renderer)
//    {
//        return animatable.getModel();
//    }
//
//    @Override
//    public ResourceLocation getTextureResource(AbstractGolemDandoriPik animatable, @Nullable GeoRenderer<AbstractGolemDandoriPik> renderer)
//    {
//        return animatable.getTexture();
//    }
//
//    @Override
//    public ResourceLocation getAnimationResource(AbstractGolemDandoriPik animatable)
//    {
//        return animatable.getAnimations();
//    }

//    @Override
//    public void setCustomAnimations(AbstractGolemDandoriPik animatable, long instanceId, AnimationState<AbstractGolemDandoriPik> animationState)
//    {
//
//    }
}
