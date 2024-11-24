package com.kitsplode.golemdandori2.geckolib.client.model.entity;

import com.kitsplode.golemdandori2.ExampleModCommon;
import com.kitsplode.golemdandori2.geckolib.client.renderer.entity.ParasiteRenderer;
import com.kitsplode.golemdandori2.geckolib.entity.ParasiteEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link ParasiteEntity}
 * @see ParasiteRenderer
 */
public class ParasiteModel extends DefaultedEntityGeoModel<ParasiteEntity> {
	public ParasiteModel() {
		super(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "parasite"));
	}

	// We want our model to render using the translucent render type
	@Override
	public RenderType getRenderType(ParasiteEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}