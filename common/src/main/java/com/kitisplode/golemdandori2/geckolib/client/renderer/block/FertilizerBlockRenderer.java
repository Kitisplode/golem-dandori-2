package com.kitisplode.golemdandori2.geckolib.client.renderer.block;

import com.kitisplode.golemdandori2.geckolib.block.entity.FertilizerBlockEntity;
import com.kitisplode.golemdandori2.geckolib.client.model.block.FertilizerModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Example {@link net.minecraft.world.level.block.entity.BlockEntity} renderer for {@link FertilizerBlockEntity}
 * @see FertilizerModel
 * @see FertilizerBlockEntity
 */
public class FertilizerBlockRenderer extends GeoBlockRenderer<FertilizerBlockEntity> {
	public FertilizerBlockRenderer() {
		super(new FertilizerModel());
	}
}
