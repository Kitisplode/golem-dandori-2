package com.kitsplode.golemdandori2.entity.golem;

import com.kitsplode.golemdandori2.ExampleModCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;

abstract public class AbstractGolemDandoriPik extends AbstractGolem implements GeoEntity
{

    protected AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    protected AbstractGolemDandoriPik(EntityType<? extends AbstractGolem> entityType, Level level)
    {
        super(entityType, level);
    }

    protected abstract String getResourcePath();
    protected abstract String getResourceName();

    public ResourceLocation getModel()
    {
        return ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "geo/" + getResourcePath() + getResourceName());
    }

    public ResourceLocation getTexture()
    {
        return ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "textures/" + getResourcePath() + getResourceName() + ".png");
    }

    public ResourceLocation getAnimations()
    {
        return ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "animations/" + getResourcePath() + getResourceName() + ".animation.json");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
