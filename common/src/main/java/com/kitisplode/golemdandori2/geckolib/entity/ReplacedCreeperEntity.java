package com.kitisplode.golemdandori2.geckolib.entity;

import com.kitisplode.golemdandori2.geckolib.client.model.entity.ReplacedCreeperModel;
import com.kitisplode.golemdandori2.geckolib.client.renderer.entity.ReplacedCreeperRenderer;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Replacement {@link net.minecraft.world.entity.monster.Creeper Creeper} {@link GeoEntity} to showcase
 * replacing the model and animations of an existing entity
 *
 * @see software.bernie.geckolib.renderer.GeoReplacedEntityRenderer GeoReplacedEntityRenderer
 * @see ReplacedCreeperRenderer ReplacedCreeperRenderer
 * @see ReplacedCreeperModel ReplacedCreeperModel
 */
public class ReplacedCreeperEntity implements GeoReplacedEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	// Register the idle + walk animations for the entity.<br>
	// In this situation we're going to use a generic controller that is already built for us
	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(DefaultAnimations.genericWalkIdleController(this));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public EntityType<?> getReplacingEntityType() {
		return EntityType.CREEPER;
	}
}
