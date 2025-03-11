package com.kitisplode.golemdandori2.entity.golem.legends;

import com.kitisplode.golemdandori2.entity.goal.action.GoalMultiStageAttack;
import com.kitisplode.golemdandori2.entity.goal.action.GoalMultiStageMine;
import com.kitisplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import com.kitisplode.golemdandori2.registry.SoundRegistry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.function.Supplier;


public class EntityGolemMossy extends AbstractGolemDandoriPik
{
    private static final int STATE_DAMAGE_ATTACK = 2;

    public EntityGolemMossy(EntityType<? extends AbstractGolem> entityType, Level level)
    {
        super(entityType, level);
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.MAX_HEALTH, 70.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75f)
                .add(Attributes.ATTACK_DAMAGE, 0.1f)
                .add(Attributes.ATTACK_KNOCKBACK, 0.1f);
    }

    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(10, new GoalMultiStageAttack(this, 0.8, true, 6, 0, new int[]{10,5}, 2, DANDORI_ACTIVITIES.COMBAT));
        this.goalSelector.addGoal(15, new GoalMultiStageMine(this, 0.8, true, 7, 0, new int[]{30,20,10}, 2));

        // If idle, then combat is less important than following deployment orders
        this.goalSelector.addGoal(35, new GoalMultiStageAttack(this, 0.8, true, 6, 0, new int[]{10,5}, 2, DANDORI_ACTIVITIES.IDLE));
    }

    @Override
    public boolean tryAct()
    {
        return false;
    }

    @Override
    public boolean canMine()
    {
        return true;
    }

    @Override
    protected int getMineStrength()
    {
        return 24;
    }

    @Override
    public boolean tryMine()
    {
        if (getCurrentState() != STATE_DAMAGE_ATTACK) return false;

        if (getMinePosition() != null)
        {
            helperMineBlock(getMinePosition());
            playSound(SOUND_ACT.get(), this.getSoundVolume() * 0.15f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.1F);
            if (getMinePosition() != null)
            {
                BlockState bs = this.level().getBlockState(getMinePosition());
                if (!bs.isAir())
                {
                    this.playSound(bs.getSoundType().getHitSound(), 1, 1);
                }
            }
        }
        return true;
    }

    // --------------------------------------------------------------------------------------------
    // Animation stuff

    private static final String RESOURCE_NAME = "golem_mossy";

    protected String getResourcePath() {return "entity/legends/";}
    protected String getResourceName() {return RESOURCE_NAME;}

    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation." + RESOURCE_NAME + ".walk");
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation." + RESOURCE_NAME + ".idle");
    private static final RawAnimation ANIMATION_ATTACK = RawAnimation.begin().then("animation." + RESOURCE_NAME + ".attack", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_WINDUP = RawAnimation.begin().then("animation." + RESOURCE_NAME + ".attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_MINING_WINDUP = RawAnimation.begin().then("animation." + RESOURCE_NAME + ".mine_windup", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_MINING = RawAnimation.begin().then("animation." + RESOURCE_NAME + ".mine", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_MINING_END = RawAnimation.begin().then("animation." + RESOURCE_NAME + ".mine_end", Animation.LoopType.HOLD_ON_LAST_FRAME);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemMossy pGolem = event.getAnimatable();
            int state = pGolem.getCurrentState();
            int activity = pGolem.getDandoriActivity();
            if (state > 0)
            {
                event.getController().setAnimationSpeed(2.00);
                if (activity == DANDORI_ACTIVITIES.MINING.ordinal())
                {
                    if (state == 1) return event.setAndContinue(ANIMATION_MINING_WINDUP);
                    if (state == 2) return event.setAndContinue(ANIMATION_MINING);
                    return event.setAndContinue(ANIMATION_MINING_END);
                }
                if (pGolem.getCurrentState() == 1) return event.setAndContinue(ANIMATION_WINDUP);
                return event.setAndContinue(ANIMATION_ATTACK);
            }
            else
            {
                event.getController().setAnimationSpeed(1.00);
                if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                    return event.setAndContinue(ANIMATION_WALK);
            }
            return event.setAndContinue(ANIMATION_IDLE);
        }));
    }

    // =================================================================================================================
    // Audio
    protected static final Supplier<SoundEvent> SOUND_YES = SoundRegistry.ENTITY_GOLEM_COBBLE_YES;
    protected static final Supplier<SoundEvent> SOUND_ACT = SoundRegistry.ENTITY_GOLEM_COBBLE_ARMS;
    protected static final Supplier<SoundEvent> SOUND_ORDERED = SoundRegistry.ENTITY_GOLEM_COBBLE_ORDERED;

//    @Override
//    protected SoundEvent getAmbientSound() {
//        return SoundRegistry.ENTITY_GOLEM_COBBLE_IDLE.get();
//    }

    @Override
    public void playSoundYes()
    {
        this.playSound(SOUND_YES.get(), this.getSoundVolume() * 0.45f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.1F);
    }

    @Override
    public void playSoundOrdered()
    {
        this.playSound(SOUND_ORDERED.get(), this.getSoundVolume() * 0.25f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.1F);
    }

}
