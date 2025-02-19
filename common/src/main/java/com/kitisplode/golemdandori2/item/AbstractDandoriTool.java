package com.kitisplode.golemdandori2.item;

import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriLeader;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import com.kitisplode.golemdandori2.util.DataDandoriCount;
import com.kitisplode.golemdandori2.util.ExtraMath;
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

import java.util.List;

abstract public class AbstractDandoriTool extends Item implements IItemSwingUse
{
    static protected final double DANDORI_RANGE = 10;
    static protected final int MAX_USE_TIME = 40;
    static protected final double MAX_ORDER_RANGE = 48;
    protected double currentRange = 1;

    static private final MobEffectInstance GLOW_EFFECT = new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false);

    public AbstractDandoriTool(Properties properties)
    {
        super(properties);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, LivingEntity entity)
    {
        return MAX_USE_TIME;
    }

    @Override
    @NotNull
    public InteractionResult use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand)
    {
//        if (!pLevel.isClientSide())
//        {
//            if (!pPlayer.isCrouching())
//            {
//                int _dandoriCount = dandoriWhistle(pLevel, pPlayer, false, IEntityDandoriPik.DANDORI_STATES.HARD);
//            }
//            else
//            {
//                int _dandoriCount = dandoriWhistle(pLevel, pPlayer, true, IEntityDandoriPik.DANDORI_STATES.OFF);
//            }
//        }

        pPlayer.startUsingItem(pUsedHand);
        pPlayer.swing(pUsedHand);
        currentRange = 1;
        return InteractionResult.PASS;
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration)
    {
        assert(pLivingEntity instanceof Player);
        Player pPlayer = (Player) pLivingEntity;
        int currentUseDuration = MAX_USE_TIME - pRemainingUseDuration;
        currentRange = Mth.lerp((double) currentUseDuration / MAX_USE_TIME, 1, DANDORI_RANGE);

        if (!pLevel.isClientSide())
        {
            if (!pPlayer.isCrouching())
            {
                int _dandoriCount = dandoriCall(pLevel, pPlayer, true, IEntityDandoriPik.DANDORI_STATES.HARD, currentRange);
            }
            else
            {
                int _dandoriCount = dandoriCall(pLevel, pPlayer, true, IEntityDandoriPik.DANDORI_STATES.OFF, DANDORI_RANGE*2);
            }
        }
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
            BlockHitResult ray = ExtraMath.playerRaycast(pPlayer, fh, MAX_ORDER_RANGE);
            if (pPlayer.distanceToSqr(ray.getLocation()) <= Mth.square(MAX_ORDER_RANGE))
            {
                int count = dandoriAct(pPlayer.level(), pPlayer, ray.getBlockPos(), false, _currentType, DANDORI_RANGE, 1);
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




    protected int dandoriCall(Level level, LivingEntity caller, boolean forced, IEntityDandoriPik.DANDORI_STATES dandoriState, double range)
    {
        return dandoriCall(level, caller, forced, dandoriState, range, -1);
    }

    protected int dandoriCall(Level level, LivingEntity caller, boolean forced, IEntityDandoriPik.DANDORI_STATES dandoriState, double range, int count)
    {
        int _targetCount = 0;
        List<Mob> targetList = level.getEntitiesOfClass(Mob.class, caller.getBoundingBox().inflate(range));
        for (Mob target : targetList)
        {
            if (_targetCount > count && count > 0) break;
            // Skip the item user.
            if (target == caller) continue;
            // Skip anything the user is currently riding.
            if (target.hasPassenger(caller)) continue;
            // Skip anything that doesn't follow dandori rules.
            if (!(target instanceof IEntityDandoriPik)) continue;

            IEntityDandoriPik dandoriTarget = (IEntityDandoriPik) target;

            // Skip piks that already have the same dandori mode.
            if (dandoriTarget.getDandoriState() == dandoriState.ordinal()) continue;
            // If the pik has a different owner than the user, skip.
            if (dandoriTarget.getOwner() != caller) continue;

            _targetCount++;

            // If the pik doesn't have a target, or if we're forcing it, set the pik's dandori mode.
            if (target.getTarget() == null || forced)
            {
                dandoriTarget.setDandoriState(dandoriState.ordinal());
                if (dandoriState != IEntityDandoriPik.DANDORI_STATES.OFF)
                {
                    target.addEffect(new MobEffectInstance(GLOW_EFFECT));
                    dandoriTarget.playSoundYes();
                }
            }
        }
        if (_targetCount > 0)
        {
            ((IEntityDandoriLeader) caller).setRecountDandori();
        }
        return _targetCount;
    }

    protected int dandoriAct(Level level, LivingEntity caller, BlockPos targetPosition, boolean forced, DataDandoriCount.FOLLOWER_TYPE currentType, double range, int count)
    {

        if (targetPosition == null) return 0;

        int _targetCount = 0;
        List<Mob> targetList = level.getEntitiesOfClass(Mob.class, caller.getBoundingBox().inflate(range));
        for (Mob target : targetList)
        {
            // Stop if we've already deployed the requested number of piks.
            if (_targetCount >= count && count > 0) break;
            // Skip the item user.
            if (target == caller) continue;
            // Skip anything that doesn't follow dandori rules
            if (!(target instanceof IEntityDandoriPik)) continue;
            IEntityDandoriPik dandoriTarget = (IEntityDandoriPik) target;
            // Skip piks that are not in dandori mode, unless we're forcing dandori.
            if (dandoriTarget.isDandoriOff() && !forced) continue;
            // If the thing has an owner, skip ones unless we are the owner.
            if (dandoriTarget.getOwner() != caller) continue;
            // SKip anything that isn't of the player's currently selected type.
            if (!DataDandoriCount.entityIsOfType(currentType, target)) continue;

            _targetCount++;
            // Have the pik perform the action
            if (!level.isClientSide())
            {
                dandoriActPerPik(target, targetPosition);
            }
        }
        if (_targetCount > 0)
        {
            ((IEntityDandoriLeader) caller).setRecountDandori();
        }
        return _targetCount;
    }

    abstract protected void dandoriActPerPik(Mob pik, BlockPos targetPosition);
}
