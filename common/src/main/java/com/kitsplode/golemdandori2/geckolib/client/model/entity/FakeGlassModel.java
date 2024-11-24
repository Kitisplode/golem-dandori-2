package com.kitsplode.golemdandori2.geckolib.client.model.entity;

import com.kitsplode.golemdandori2.ExampleModCommon;
import com.kitsplode.golemdandori2.geckolib.client.renderer.entity.FakeGlassRenderer;
import com.kitsplode.golemdandori2.geckolib.entity.FakeGlassEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * Example {@link GeoModel} for the {@link FakeGlassEntity}
 * @see FakeGlassRenderer
 */
public class FakeGlassModel extends DefaultedEntityGeoModel<FakeGlassEntity> {
	private static final ResourceLocation REDSTONE_BLOCK_TEXTURE =
			ResourceLocation.withDefaultNamespace("textures/block/redstone_block.png");

	public FakeGlassModel() {
		super(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "fake_glass"));
	}

	// We just want our texture to be the Redstone Block texture
	@Override
	public ResourceLocation getTextureResource(FakeGlassEntity animatable, @Nullable GeoRenderer<FakeGlassEntity> renderer) {
		return REDSTONE_BLOCK_TEXTURE;
	}

	// We want our entity to be translucent
	@Override
	public RenderType getRenderType(FakeGlassEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}
