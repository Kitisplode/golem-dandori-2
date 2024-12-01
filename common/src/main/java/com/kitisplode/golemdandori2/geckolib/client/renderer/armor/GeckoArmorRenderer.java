package com.kitisplode.golemdandori2.geckolib.client.renderer.armor;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.geckolib.item.GeckoArmorItem;
import com.kitisplode.golemdandori2.geckolib.item.WolfArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/**
 * Example {@link GeoRenderer} for the {@link WolfArmorItem} example item
 */
public final class GeckoArmorRenderer extends GeoArmorRenderer<GeckoArmorItem> {
	public GeckoArmorRenderer() {
		super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "armor/gecko_armor")));

		addRenderLayer(new AutoGlowingGeoLayer<>(this));
	}
}
