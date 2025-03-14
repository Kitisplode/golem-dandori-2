package com.kitisplode.golemdandori2.client.renderer.entity;

import com.kitisplode.golemdandori2.client.model.entity.ModelGolem;
import com.kitisplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RendererGolem extends GeoEntityRenderer<AbstractGolemDandoriPik>
{
    public RendererGolem(EntityRendererProvider.Context context)
    {
        super(context, new ModelGolem());
    }

    public RendererGolem(EntityRendererProvider.Context context, ModelGolem model)
    {
        super(context, model);
    }

    @Override
    public void render(EntityRenderState entityRenderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
    {
        super.render(entityRenderState, poseStack, bufferSource, packedLight);
        if (this.model instanceof ModelGolem modelGolem) modelGolem.resetCustomAnimations();
    }
}
