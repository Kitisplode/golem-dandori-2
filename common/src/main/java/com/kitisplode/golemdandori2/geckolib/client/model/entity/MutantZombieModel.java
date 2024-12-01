package com.kitisplode.golemdandori2.geckolib.client.model.entity;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.geckolib.client.renderer.entity.MutantZombieRenderer;
import com.kitisplode.golemdandori2.geckolib.entity.DynamicExampleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link DynamicExampleEntity}
 * @see MutantZombieRenderer
 */
public class MutantZombieModel extends DefaultedEntityGeoModel<DynamicExampleEntity> {
	public MutantZombieModel() {
		super(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "mutant_zombie"));
	}
}