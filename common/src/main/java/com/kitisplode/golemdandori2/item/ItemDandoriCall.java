package com.kitisplode.golemdandori2.item;

import com.kitisplode.golemdandori2.client.renderer.item.RendererItemDandoriBanner;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriLeader;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import com.kitisplode.golemdandori2.util.DataDandoriCount;
import com.kitisplode.golemdandori2.util.ExtraMath;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
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
    static protected final double maxAttackRange = 48;

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

    @Override
    public void swing(Player pPlayer)
    {
        Level _level = pPlayer.level();
        if (pPlayer.isCrouching())
        {
            if (pPlayer instanceof IEntityDandoriLeader _leader && !_level.isClientSide()) _leader.nextDandoriCurrentType();
        }
        else
        {
            DataDandoriCount.FOLLOWER_TYPE _currentType = ((IEntityDandoriLeader) pPlayer).getDandoriCurrentType();
            ClipContext.Fluid fh = ClipContext.Fluid.ANY;
            if (pPlayer.isUnderWater()) fh = ClipContext.Fluid.NONE;
            BlockHitResult ray = ExtraMath.playerRaycast(pPlayer, fh, maxAttackRange);
            if (pPlayer.distanceToSqr(ray.getLocation()) <= Mth.square(maxAttackRange))
            {
                int count = dandoriDeploy(pPlayer.level(), pPlayer, ray.getBlockPos(), false, _currentType, 1);
//            if (count == 0) pPlayer.playSound(ModSounds.ITEM_DANDORI_ATTACK_FAIL.get(), 1.0f, 0.9f);
//            else
//            {
//                pPlayer.playSound(ModSounds.ITEM_DANDORI_ATTACK_WIN.get(), 1.0f, 1.0f);
//                effectDeploy(world, 20, 6, ray.getLocation());
//            }
                pPlayer.swing(InteractionHand.MAIN_HAND);
            }
        }
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
        if (_targetCount > 0)
        {
            ((IEntityDandoriLeader) pLeader).setRecountDandori();
        }
        return _targetCount;
    }

    protected int dandoriDeploy(Level world, LivingEntity pLeader, BlockPos position, boolean forceDandori, DataDandoriCount.FOLLOWER_TYPE currentType, int count)
    {
        if (position == null) return 0;

        int _targetCount = 0;
        List<Mob> targetList = world.getEntitiesOfClass(Mob.class, pLeader.getBoundingBox().inflate(maxAttackRange));
        for (Mob target : targetList)
        {
            // Stop if we've already deployed the requested number of piks.
            if (_targetCount >= count && count > 0) break;
            // Skip the item user.
            if (target == pLeader) continue;
            // Skip anything that doesn't follow dandori rules
            if (!(target instanceof IEntityDandoriPik)) continue;
            IEntityDandoriPik dandoriTarget = (IEntityDandoriPik) target;
            // Skip piks that are not in dandori mode, unless we're forcing dandori.
            if (dandoriTarget.isDandoriOff() && !forceDandori) continue;
            // If the thing has an owner, skip ones unless we are the owner.
            if (dandoriTarget.getOwner() != pLeader) continue;
            // SKip anything that isn't of the player's currently selected type.
            if (!DataDandoriCount.entityIsOfType(currentType, target)) continue;

            _targetCount++;
            // Deploy the pik to the given location.
            if (!world.isClientSide())
            {
                dandoriTarget.setDandoriState(IEntityDandoriPik.DANDORI_STATES.OFF.ordinal());
                dandoriTarget.setDeployPosition(position);
            }
        }
        if (_targetCount > 0)
        {
            ((IEntityDandoriLeader) pLeader).setRecountDandori();
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
