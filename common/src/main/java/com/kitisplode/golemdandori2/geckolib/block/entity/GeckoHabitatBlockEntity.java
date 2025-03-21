package com.kitisplode.golemdandori2.geckolib.block.entity;

import com.kitisplode.golemdandori2.geckolib.client.model.block.GeckoHabitatModel;
import com.kitisplode.golemdandori2.geckolib.client.renderer.block.GeckoHabitatBlockRenderer;
import com.kitisplode.golemdandori2.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Example {@link BlockEntity} implementation using a GeckoLib model.
 * @see GeckoHabitatModel
 * @see GeckoHabitatBlockRenderer
 */
public class GeckoHabitatBlockEntity extends BlockEntity implements GeoBlockEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public GeckoHabitatBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.GECKO_HABITAT.get(), pos, state);
	}

	// We just want a permanent idle animation happening here
	// But if it's day time we want him to take a nap
	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, state -> {
			if (getLevel().getDayTime() > 23000 || getLevel().getDayTime() < 13000) {
				return state.setAndContinue(DefaultAnimations.REST);
			}
			else {
				return state.setAndContinue(DefaultAnimations.IDLE);
			}
		}));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
