package com.kitisplode.golemdandori2.entity.golem.legends;

import com.kitisplode.golemdandori2.entity.goal.action.GoalMultiStageAttack;
import com.kitisplode.golemdandori2.entity.goal.action.GoalMultiStageMine;
import com.kitisplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import com.kitisplode.golemdandori2.entity.projectile.EntityProjectileOwnerAware;
import com.kitisplode.golemdandori2.registry.SoundRegistry;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.function.Supplier;

public class EntityGolemPlank extends AbstractGolemDandoriPik
{
    private static final double ATTACK_RANGE = Mth.square(12);

    public EntityGolemPlank(EntityType<? extends AbstractGolem> entityType, Level level)
    {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.MAX_HEALTH, 40.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75f)
                .add(Attributes.ATTACK_DAMAGE, 0.5f)
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
        this.goalSelector.addGoal(10, new GoalMultiStageAttack(this, 0.8, true, ATTACK_RANGE, 0, new int[]{35,10}, 2, DANDORI_ACTIVITIES.COMBAT));
        this.goalSelector.addGoal(15, new GoalMultiStageMine(this, 0.8, true, 7, 0, new int[]{30,20,10}, 2));

        // If idle, then combat is less important than following deployment orders
        this.goalSelector.addGoal(35, new GoalMultiStageAttack(this, 0.8, true, ATTACK_RANGE, 0, new int[]{35,10}, 2, DANDORI_ACTIVITIES.IDLE));
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
            _arrow.setArrowOwner(this);
            _arrow.setPos(this.getEyePosition());
            _arrow.setDeltaMovement(_shootingVelocity);
            _arrow.setYRot(this.getYRot());
            _arrow.setBaseDamage(0.05f);
//            _arrow.setAoeRange(0.0);
//            _arrow.setAoeDamage(0.0);
//            _arrow.setHasAoE(false);
            _arrow.setNoGravity(false);
            _serverLevel.addFreshEntity(_arrow);
        }
    }

    @Override
    public boolean canMine()
    {
        return true;
    }
    @Override
    public boolean tryMine()
    {
        if (getCurrentState() != 2) return false;

        if (getMinePosition() != null)
        {
            helperMineBlock(getMinePosition());
            playSound(SOUND_MINE.get(), this.getSoundVolume() * 0.15f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.1F);
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

    private static final String RESOURCE_NAME = "golem_plank";

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
            EntityGolemPlank pGolem = event.getAnimatable();
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
                if (state == 1) return event.setAndContinue(ANIMATION_WINDUP);
                return event.setAndContinue(ANIMATION_ATTACK);
            }
            event.getController().setAnimationSpeed(1.00);
            if (pGolem.getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving()) return event.setAndContinue(ANIMATION_WALK);
            return event.setAndContinue(ANIMATION_IDLE);
        }));
    }

    // --------------------------------------------------------------------------------------------
    // Audio stuff
    protected static final Supplier<SoundEvent> SOUND_YES = SoundRegistry.ENTITY_GOLEM_PLANK_YES;
    protected static final Supplier<SoundEvent> SOUND_ACT = SoundRegistry.ENTITY_GOLEM_PLANK_SHOOT;
    protected static final Supplier<SoundEvent> SOUND_ORDERED = SoundRegistry.ENTITY_GOLEM_PLANK_ORDERED;
    protected static final Supplier<SoundEvent> SOUND_MINE = SoundRegistry.ENTITY_GOLEM_PLANK_MINE;

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
