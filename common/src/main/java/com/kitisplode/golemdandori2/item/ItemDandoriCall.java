package com.kitisplode.golemdandori2.item;

import com.kitisplode.golemdandori2.client.renderer.item.RendererItemDandoriBanner;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ItemDandoriCall extends AbstractDandoriTool implements GeoItem
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ItemDandoriCall(Properties properties)
    {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    protected void dandoriActPerPik(Mob mob, BlockPos targetPosition)
    {
        if (mob instanceof IEntityDandoriPik pik)
        {
            pik.setDandoriState(IEntityDandoriPik.DANDORI_STATES.OFF.ordinal());
            pik.setDeployPosition(targetPosition);
        }
    }

    // =================================================================================================================
    // Animation

    // Create our model/renderer and return it
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private RendererItemDandoriBanner renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new RendererItemDandoriBanner();
                // Defer creation of our renderer then cache it so that it doesn't get instantiated too early

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 0, event ->
        {
            return event.setAndContinue(RawAnimation.begin().then("animation.banner_courage.idle", Animation.LoopType.LOOP));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
