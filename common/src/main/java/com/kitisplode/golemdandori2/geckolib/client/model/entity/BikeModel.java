package com.kitisplode.golemdandori2.geckolib.client.model.entity;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.geckolib.client.renderer.entity.BikeRenderer;
import com.kitisplode.golemdandori2.geckolib.entity.BikeEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link BikeEntity}
 * @see BikeRenderer
 */
public class BikeModel extends DefaultedEntityGeoModel<BikeEntity> {
	public BikeModel() {
		super(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "bike"));
	}

	// We want this entity to have a translucent render
	@Override
	public RenderType getRenderType(BikeEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}
