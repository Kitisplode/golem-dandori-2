package com.kitisplode.golemdandori2.entity.golem.legends;

import com.kitisplode.golemdandori2.entity.goal.action.GoalMultiStageAttack;
import com.kitisplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import com.kitisplode.golemdandori2.entity.projectile.EntityProjectileOwnerAware;
import com.kitisplode.golemdandori2.registry.EntityRegistry;
import com.kitisplode.golemdandori2.registry.SoundRegistry;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.function.Supplier;

public class EntityGolemPlank extends AbstractGolemDandoriPik
{
    private static final double ATTACK_RANGE = Mth.square(12);

    protected static final Supplier<SoundEvent> SOUND_YES = SoundRegistry.ENTITY_GOLEM_PLANK_YES;
    protected static final Supplier<SoundEvent> SOUND_ACT = SoundRegistry.ENTITY_GOLEM_PLANK_SHOOT;
    protected static final Supplier<SoundEvent> SOUND_ORDERED = SoundRegistry.ENTITY_GOLEM_PLANK_ORDERED;

    public EntityGolemPlank(EntityType<? extends AbstractGolem> entityType, Level level)
    {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.MAX_HEALTH, 50.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75f)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(Attributes.ATTACK_KNOCKBACK, 0.1f);
    }

    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(10, new GoalMultiStageAttack(this, 0.8, true, ATTACK_RANGE, 0, new int[]{30,10}, 2, DANDORI_ACTIVITIES.COMBAT));

        // If idle, then combat is less important than following deployment orders
        this.goalSelector.addGoal(35, new GoalMultiStageAttack(this, 0.8, true, ATTACK_RANGE, 0, new int[]{30,10}, 2, DANDORI_ACTIVITIES.IDLE));
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
        if (getCurrentState() != 2) return false;
        if (getTarget() != null)
        {
            shootProjectile(getTarget());
            playSound(SOUND_ACT.get(), this.getSoundVolume() * 0.15f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.1F);
        }
        return true;
    }
    private void shootProjectile(LivingEntity _target)
    {
        if (_target == null || !_target.isAlive()) return;
        if (this.level() instanceof ServerLevel _serverLevel)
        {
//            AbstractArrow _arrow = new Arrow(EntityType.ARROW, _serverLevel);
            EntityProjectileOwnerAware _arrow = new EntityProjectileOwnerAware(EntityType.ARROW, _serverLevel);


            Vec3 _shootingVelocity = _target.getEyePosition().subtract(this.getEyePosition()).normalize().scale(3.0f);
            _arrow.setOwner(this);
            _arrow.setPos(this.getEyePosition());
            _arrow.setDeltaMovement(_shootingVelocity);
            _arrow.setYRot(this.getYRot());
            _arrow.setBaseDamage(2);
//            _arrow.setAoeRange(0.0);
//            _arrow.setAoeDamage(0.0);
//            _arrow.setHasAoE(false);
            _arrow.setNoGravity(false);
            _serverLevel.addFreshEntity(_arrow);
        }
    }

    // Plank golem cannot mine
    @Override
    public boolean canMine()
    {
        return false;
    }
    @Override
    public boolean tryMine()
    {
        return false;
    }

    // --------------------------------------------------------------------------------------------
    // Animation stuff

    private static final String RESOURCE_NAME = "golem_plank";

    protected String getResourcePath() {return "entity/legends/";}
    protected String getResourceName() {return RESOURCE_NAME;}

    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation." + RESOURCE_NAME + ".walk");
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation." + RESOURCE_NAME + ".idle");
    private static final RawAnimation ANIMATION_ATTACK = RawAnimation.begin().then("animation." + RESOURCE_NAME + ".attack", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_WINDUP = RawAnimation.begin().then("animation." + RESOURCE_NAME + ".attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemPlank pGolem = event.getAnimatable();
            if (pGolem.getCurrentState() > 0)
            {
                if (pGolem.getCurrentState() == 1)
                {
                    event.getController().setAnimationSpeed(2.00);
                    return event.setAndContinue(ANIMATION_WINDUP);
                }
                else
                {
                    event.getController().setAnimationSpeed(2.00);
                    return event.setAndContinue(ANIMATION_ATTACK);
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

    // --------------------------------------------------------------------------------------------
    // Audio stuff
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
