package com.kitisplode.golemdandori2.geckolib.entity;

import com.kitisplode.golemdandori2.geckolib.client.model.entity.FakeGlassModel;
import com.kitisplode.golemdandori2.geckolib.client.renderer.entity.FakeGlassRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Example {@link GeoAnimatable} implementation of an entity that uses the texture-per-bone feature of {@link software.bernie.geckolib.renderer.specialty.DynamicGeoEntityRenderer DynamicGeoEntityRenderer}
 *
 * @see FakeGlassModel FakeGlassModel
 * @see FakeGlassRenderer FakeGlassRenderer
 */
public class FakeGlassEntity extends PathfinderMob implements GeoEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public FakeGlassEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
		super(entityType, level);
	}

	// We don't care about animations for this one
	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
