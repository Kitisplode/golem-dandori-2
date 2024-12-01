package com.kitisplode.golemdandori2.item;

import com.kitisplode.golemdandori2.client.renderer.item.RendererItemDandoriBanner;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

import java.util.List;
import java.util.function.Consumer;

public class ItemDandoriCall extends Item implements IItemSwingUse, GeoItem
{
    static protected final double dandoriRange = 10;
    static protected final int maxUseTime = 40;


    static protected final MobEffectInstance glowEffect = new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ItemDandoriCall(Properties properties)
    {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, LivingEntity entity)
    {
        return maxUseTime;
    }

    @Override
    @NotNull
    public InteractionResult use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand)
    {
        if (!pLevel.isClientSide())
        {
            if (!pPlayer.isCrouching())
            {
                int _dandoriCount = dandoriWhistle(pLevel, pPlayer, false, IEntityDandoriPik.DANDORI_STATES.HARD);
            }
            else
            {
                int _dandoriCount = dandoriWhistle(pLevel, pPlayer, true, IEntityDandoriPik.DANDORI_STATES.OFF);
            }
        }

        pPlayer.startUsingItem(pUsedHand);
        pPlayer.swing(pUsedHand);
        return InteractionResult.PASS;
    }

    // Helpers
    protected int dandoriWhistle(Level pLevel, LivingEntity pLeader, boolean pForce, IEntityDandoriPik.DANDORI_STATES pDandoriValue)
    {
        int _targetCount = 0;
        List<Mob> targetList = pLevel.getEntitiesOfClass(Mob.class, pLeader.getBoundingBox().inflate(dandoriRange));
        for (Mob target : targetList)
        {
            // Skip the item user.
            if (target == pLeader) continue;
            // Skip anything the user is currently riding.
            if (target.hasPassenger(pLeader)) continue;
            // Skip anything that doesn't follow dandori rules.
            if (!(target instanceof IEntityDandoriPik)) continue;

            IEntityDandoriPik dandoriTarget = (IEntityDandoriPik) target;

            // Skip piks that already have the same dandori mode.
            if (dandoriTarget.getDandoriState() == pDandoriValue.ordinal()) continue;
            // If the pik has a different owner than the user, skip.
            if (dandoriTarget.getOwner() != pLeader) continue;

            _targetCount++;

            // If the pik doesn't have a target, or if we're forcing it, set the pik's dandori mode.
            if (target.getTarget() == null || pForce)
            {
                dandoriTarget.setDandoriState(pDandoriValue.ordinal());
                if (pDandoriValue != IEntityDandoriPik.DANDORI_STATES.OFF)
                {
                    target.addEffect(new MobEffectInstance(glowEffect));
                }
            }
        }
        return _targetCount;
    }

    // =================================================================================================================
    // Animation

    // Create our model/renderer and return it
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private RendererItemDandoriBanner renderer;

            @Override
            @Nullable
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
