package com.kitisplode.golemdandori2.geckolib.client.renderer.entity;

import com.kitisplode.golemdandori2.geckolib.client.model.entity.RaceCarModel;
import com.kitisplode.golemdandori2.geckolib.entity.RaceCarEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Example {@link software.bernie.geckolib.renderer.GeoRenderer} implementation of an entity
 * @see RaceCarModel
 * @see RaceCarEntity
 */
public class RaceCarRenderer extends GeoEntityRenderer<RaceCarEntity> {
	public RaceCarRenderer(EntityRendererProvider.Context context) {
		super(context, new RaceCarModel());
	}
}