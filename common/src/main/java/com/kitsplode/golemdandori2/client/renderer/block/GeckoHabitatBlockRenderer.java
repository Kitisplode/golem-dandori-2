package com.kitsplode.golemdandori2.client.renderer.block;

import com.kitsplode.golemdandori2.block.entity.GeckoHabitatBlockEntity;
import com.kitsplode.golemdandori2.client.model.block.GeckoHabitatModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Example {@link net.minecraft.world.level.block.entity.BlockEntity BlockEntity} renderer for {@link GeckoHabitatBlockEntity}
 * @see GeckoHabitatModel
 * @see GeckoHabitatBlockEntity
 */
public class GeckoHabitatBlockRenderer extends GeoBlockRenderer<GeckoHabitatBlockEntity> {
	public GeckoHabitatBlockRenderer() {
		super(new GeckoHabitatModel());
	}
}
