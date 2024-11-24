package com.kitsplode.golemdandori2.geckolib.client.model.entity;

import com.kitsplode.golemdandori2.ExampleModCommon;
import com.kitsplode.golemdandori2.geckolib.entity.DynamicExampleEntity;
import com.kitsplode.golemdandori2.geckolib.client.renderer.entity.GremlinRenderer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link DynamicExampleEntity}
 * @see GremlinRenderer GremlinRenderer
 */
public class GremlinModel extends DefaultedEntityGeoModel<DynamicExampleEntity> {
	public GremlinModel() {
		super(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "gremlin"));
	}
}