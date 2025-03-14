package com.kitisplode.golemdandori2.client.renderer.entity;

import com.kitisplode.golemdandori2.client.model.entity.ModelGolemGrindstone;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RendererGolemGrindstone extends RendererGolem
{
    public RendererGolemGrindstone(EntityRendererProvider.Context context)
    {
        super(context, new ModelGolemGrindstone());
    }
}
