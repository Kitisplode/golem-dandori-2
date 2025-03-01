package com.kitisplode.golemdandori2.client.model.entity;

import com.kitisplode.golemdandori2.entity.projectile.EntityProjectileOwnerAware;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class ModelArrow extends GeoModel<EntityProjectileOwnerAware>
{
    @Override
    public ResourceLocation getModelResource(EntityProjectileOwnerAware entityProjectileOwnerAware,  GeoRenderer<EntityProjectileOwnerAware> geoRenderer)
    {
        return entityProjectileOwnerAware.getModel();
    }

    @Override
    public ResourceLocation getTextureResource(EntityProjectileOwnerAware entityProjectileOwnerAware, GeoRenderer<EntityProjectileOwnerAware> geoRenderer)
    {
        return entityProjectileOwnerAware.getTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(EntityProjectileOwnerAware entityProjectileOwnerAware)
    {
        return null;
    }

    @Override
    public void setCustomAnimations(EntityProjectileOwnerAware entityProjectileOwnerAware, long id, software.bernie.geckolib.animation.AnimationState<EntityProjectileOwnerAware> animationState)
    {

    }
}
