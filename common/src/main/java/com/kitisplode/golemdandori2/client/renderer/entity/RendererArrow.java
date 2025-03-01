package com.kitisplode.golemdandori2.client.renderer.entity;

import com.kitisplode.golemdandori2.client.model.entity.ModelArrow;
import com.kitisplode.golemdandori2.entity.projectile.EntityProjectileOwnerAware;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RendererArrow extends GeoEntityRenderer<EntityProjectileOwnerAware>
{
    public RendererArrow(EntityRendererProvider.Context context)
    {
        super(context, new ModelArrow());
    }
}
