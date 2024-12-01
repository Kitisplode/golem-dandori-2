package com.kitisplode.golemdandori2.geckolib.client.model.block;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.geckolib.block.entity.GeckoHabitatBlockEntity;
import com.kitisplode.golemdandori2.geckolib.client.renderer.block.GeckoHabitatBlockRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link GeckoHabitatBlockEntity}
 * @see GeckoHabitatBlockEntity
 * @see GeckoHabitatBlockRenderer
 */
public class GeckoHabitatModel extends DefaultedBlockGeoModel<GeckoHabitatBlockEntity> {
	public GeckoHabitatModel() {
		super(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "gecko_habitat"));
	}

	@Override
	public RenderType getRenderType(GeckoHabitatBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}