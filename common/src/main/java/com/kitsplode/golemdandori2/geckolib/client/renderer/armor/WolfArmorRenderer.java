package com.kitsplode.golemdandori2.geckolib.client.renderer.armor;

import com.kitsplode.golemdandori2.ExampleModCommon;
import com.kitsplode.golemdandori2.geckolib.item.WolfArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * Example {@link GeoRenderer} for the {@link WolfArmorItem} example item
 */
public final class WolfArmorRenderer extends GeoArmorRenderer<WolfArmorItem> {
	public WolfArmorRenderer() {
		super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "armor/wolf_armor")));
	}
}
