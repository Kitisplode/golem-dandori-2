package com.kitisplode.golemdandori2.geckolib.client.renderer.entity;

import com.kitisplode.golemdandori2.geckolib.client.model.entity.ParasiteModel;
import com.kitisplode.golemdandori2.geckolib.entity.ParasiteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Example {@link software.bernie.geckolib.renderer.GeoRenderer} implementation of an entity
 * @see ParasiteModel
 * @see ParasiteEntity
 */
public class ParasiteRenderer extends GeoEntityRenderer<ParasiteEntity> {
	public ParasiteRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ParasiteModel());
	}
}
