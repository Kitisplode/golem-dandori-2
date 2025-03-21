package com.kitisplode.golemdandori2.geckolib.client.renderer.entity;

import com.kitisplode.golemdandori2.geckolib.client.model.entity.FakeGlassModel;
import com.kitisplode.golemdandori2.geckolib.entity.FakeGlassEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.specialty.DynamicGeoEntityRenderer;

/**
 * Example {@link DynamicGeoEntityRenderer} implementation that makes use of the per-bone texture overrides feature
 * @see FakeGlassEntity
 * @see FakeGlassModel
 */
public class FakeGlassRenderer extends DynamicGeoEntityRenderer<FakeGlassEntity> {
	private static final ResourceLocation WHITE_STAINED_GLASS_TEXTURE =
			ResourceLocation.withDefaultNamespace("textures/block/white_stained_glass.png");

	public FakeGlassRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new FakeGlassModel());
	}

	// If the bone named "outer_glass" is being rendered, render it using white stained-glass texture
	@Nullable
	@Override
	protected ResourceLocation getTextureOverrideForBone(GeoBone bone, FakeGlassEntity animatable, float partialTick) {
		return bone.getName().equals("outer_glass") ? WHITE_STAINED_GLASS_TEXTURE : null;
	}
}