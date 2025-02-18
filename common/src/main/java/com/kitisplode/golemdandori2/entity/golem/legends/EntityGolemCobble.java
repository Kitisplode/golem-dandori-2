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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.function.Supplier;


public class EntityGolemCobble extends AbstractGolemDandoriPik
{
    protected static final EntityDataAccessor<Boolean> DATA_LEFT_ARM = SynchedEntityData.defineId(EntityGolemCobble.class, EntityDataSerializers.BOOLEAN);
    private static final int stateDamageAttack = 2;

    protected static final Supplier<SoundEvent> SOUND_YES = SoundRegistry.ENTITY_GOLEM_COBBLE_YES;

    public EntityGolemCobble(EntityType<? extends AbstractGolem> entityType, Level level)
    {
        super(entityType, level);
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.MAX_HEALTH, 50.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75f)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.ATTACK_KNOCKBACK, 0.1f);
    }

    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_LEFT_ARM, random.nextBoolean());
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(10, new GoalMultiStageAttack(this, 0.8, true, 6, 0, new int[]{10,5}, 2));
        this.goalSelector.addGoal(15, new GoalMultiStageMine(this, 0.8, true, 9, 0, new int[]{10,5}, 2));
    }

    @Override
    public void tick()
    {
        super.tick();

        if (getOwner() == null)
        {
            Player _nearestPlayer = level().getNearestPlayer(this, 128);
            if (_nearestPlayer != null) setOwner(_nearestPlayer);
        }
    }

    @Override
    public boolean tryAct()
    {
        if (getCurrentState() == 0) this.entityData.set(DATA_LEFT_ARM, !this.entityData.get(DATA_LEFT_ARM));
        if (getCurrentState() != stateDamageAttack) return false;

        if (getTarget() != null)
        {
            getTarget().hurtServer((ServerLevel)this.level(), this.damageSources().mobAttack(this), 1);
            getTarget().setDeltaMovement(getTarget().getDeltaMovement().scale(0.35d));
            EnchantmentHelper.doPostAttackEffects((ServerLevel)this.level(), getTarget(), this.damageSources().mobAttack(this));

        }

        return true;
    }

    @Override
    public boolean tryMine()
    {
        if (getCurrentState() == 0) this.entityData.set(DATA_LEFT_ARM, !this.entityData.get(DATA_LEFT_ARM));
        if (getCurrentState() != stateDamageAttack) return false;

        if (getMinePosition() != null)
        {
            helperMineBlock(getMinePosition());
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

    private static final String resourceName = "golem_cobble";

    protected String getResourcePath() {return "entity/legends/";}
    protected String getResourceName() {return resourceName;}

    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation." + resourceName + ".walk");
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation." + resourceName + ".idle");
    private static final RawAnimation ANIMATION_ATTACK_LEFT = RawAnimation.begin().then("animation." + resourceName + ".attack_left", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_WINDUP_LEFT = RawAnimation.begin().then("animation." + resourceName + ".attack_windup_left", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_ATTACK_RIGHT = RawAnimation.begin().then("animation." + resourceName + ".attack_right", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_WINDUP_RIGHT = RawAnimation.begin().then("animation." + resourceName + ".attack_windup_right", Animation.LoopType.HOLD_ON_LAST_FRAME);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemCobble pGolem = event.getAnimatable();
            if (pGolem.getCurrentState() > 0)
            {
                if (pGolem.getCurrentState() == 1)
                {
                    event.getController().setAnimationSpeed(4.00);
                    if (!pGolem.entityData.get(DATA_LEFT_ARM)) return event.setAndContinue(ANIMATION_WINDUP_RIGHT);
                    return event.setAndContinue(ANIMATION_WINDUP_LEFT);
                }
                else
                {
                    event.getController().setAnimationSpeed(4.00);
                    if (!pGolem.entityData.get(DATA_LEFT_ARM)) return event.setAndContinue(ANIMATION_ATTACK_RIGHT);
                    return event.setAndContinue(ANIMATION_ATTACK_LEFT);
                }
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
//    @Override
//    protected SoundEvent getAmbientSound() {
//        return SoundRegistry.ENTITY_GOLEM_COBBLE_IDLE.get();
//    }

    @Override
    public void playSoundYes()
    {
        this.playSound(SOUND_YES.get(), this.getSoundVolume() * 0.5f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.1F);
    }

}
