package com.kitisplode.golemdandori2.geckolib.client.renderer.entity;

import com.kitisplode.golemdandori2.geckolib.client.model.entity.BikeModel;
import com.kitisplode.golemdandori2.geckolib.entity.BikeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Example {@link software.bernie.geckolib.renderer.GeoRenderer} for {@link BikeEntity}
 * @see BikeModel
 */
public class BikeRenderer extends GeoEntityRenderer<BikeEntity> {
	public BikeRenderer(EntityRendererProvider.Context context) {
		super(context, new BikeModel());
	}
}
