package com.kitsplode.golemdandori2.geckolib.client.model.entity;

import com.kitsplode.golemdandori2.ExampleModCommon;
import com.kitsplode.golemdandori2.geckolib.entity.BatEntity;
import com.kitsplode.golemdandori2.geckolib.client.renderer.entity.BatRenderer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link BatEntity}
 * @see BatRenderer BatRenderer
 */
public class BatModel extends DefaultedEntityGeoModel<BatEntity> {
	// We use the alternate super-constructor here to tell the model it should handle head-turning for us
	public BatModel() {
		super(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "bat"), true);
	}
}
