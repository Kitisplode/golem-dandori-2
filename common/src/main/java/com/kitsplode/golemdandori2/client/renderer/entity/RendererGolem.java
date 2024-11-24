package com.kitsplode.golemdandori2.client.renderer.entity;

import com.kitsplode.golemdandori2.client.model.entity.ModelGolem;
import com.kitsplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RendererGolem extends GeoEntityRenderer<AbstractGolemDandoriPik>
{
    public RendererGolem(EntityRendererProvider.Context context)
    {
        super(context, new ModelGolem());
    }
}
