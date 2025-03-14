package com.kitisplode.golemdandori2.entity.golem.legends;

import com.kitisplode.golemdandori2.entity.goal.action.GoalMultiStageAttack;
import com.kitisplode.golemdandori2.entity.goal.action.GoalMultiStageMine;
import com.kitisplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import com.kitisplode.golemdandori2.registry.SoundRegistry;
import com.kitisplode.golemdandori2.util.ExtraMath;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.function.Supplier;

public class EntityGolemGrindstone extends AbstractGolemDandoriPik
{
    private boolean isMovingBackwards = false;

    protected static final EntityDataAccessor<Float> DATA_ACCEL = SynchedEntityData.defineId(EntityGolemGrindstone.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_TURN_SPEED = SynchedEntityData.defineId(EntityGolemGrindstone.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_WHEEL_ROTATION = SynchedEntityData.defineId(EntityGolemGrindstone.class, EntityDataSerializers.FLOAT);

    private static final MobEffectInstance EFFECT_STUN = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2, false, false);
    private static final MobEffectInstance EFFECT_ARMOR = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15, 3, false, false);

    public EntityGolemGrindstone(EntityType<? extends AbstractGolem> entityType, Level level)
    {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.MAX_HEALTH, 60)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75f)
                .add(Attributes.ATTACK_DAMAGE, 0.1f)
                .add(Attributes.ATTACK_KNOCKBACK, 0.1f);
    }

    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ACCEL, 0.0f);
        builder.define(DATA_TURN_SPEED, 0.0f);
        builder.define(DATA_WHEEL_ROTATION, 0.0f);
    }

    public float maxUpStep()
    {
        return 1.2f;
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(10, new GoalMultiStageAttack(this, 0.8, true, 12*12, 0, new int[]{70,40,20}, 2, DANDORI_ACTIVITIES.COMBAT));
        this.goalSelector.addGoal(15, new GoalMultiStageMine(this, 0.8, true, 7, 0, new int[]{30,20,10}, 2));

        // If idle, then combat is less important than following deployment orders
        this.goalSelector.addGoal(35, new GoalMultiStageAttack(this, 0.8, true, 12*12, 0, new int[]{70,40,20}, 2, DANDORI_ACTIVITIES.IDLE));
    }

    @Override
    public void tick()
    {
        super.tick();
        if (getCurrentState() == 2 && getDandoriActivity() != DANDORI_ACTIVITIES.MINING.ordinal())
        {
            float angle = this.getYRot()*Mth.DEG_TO_RAD;
            Vec3 newVelocity = new Vec3(-Math.sin(angle), 0, Math.cos(angle)).scale(0.7f);
            this.setDeltaMovement(new Vec3(newVelocity.x, this.getDeltaMovement().y, newVelocity.z));
        }
    }

    @Override
    public void push(Entity target)
    {
        if (this.getCurrentState() == 2 && this.validateTarget(target))
        {
            target.hurtServer((ServerLevel)this.level(), this.damageSources().mobAttack(this), 1);
            target.setDeltaMovement(target.getDeltaMovement().scale(2.0));
            EnchantmentHelper.doPostAttackEffects((ServerLevel)this.level(), target, this.damageSources().mobAttack(this));
            playSound(SOUND_HIT.get(), this.getSoundVolume() * 0.25f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.1F);
            if (target instanceof LivingEntity livingTarget) livingTarget.addEffect(new MobEffectInstance(EFFECT_STUN));
        }
        super.push(target);
    }

    @Override
    public boolean tryAct()
    {
        if (getCurrentState() != 2) return false;
        this.addEffect(new MobEffectInstance(EFFECT_ARMOR));
        if (getTarget() != null)
        {
            float angle = (float)ExtraMath.getYawBetweenPoints(this.getPosition(0), getTarget().getPosition(0)) * Mth.RAD_TO_DEG;
            this.setYRot(angle);
            this.setYBodyRot(angle);
        }
        return false;
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
    // Riding behavior

    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        if (!this.getPassengers().isEmpty()) return super.mobInteract(player, hand);
        if (this.getOwner() != player) return super.mobInteract(player, hand);
        this.startPlayerRiding(player);
        return InteractionResult.SUCCESS_SERVER;
    }

    protected void startPlayerRiding(Player player)
    {
        if (this.level() instanceof ServerLevel)
        {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
            this.setDeployPosition(null);
            this.setDandoriState(DANDORI_STATES.OFF.ordinal());
            this.setWheelRotation(0.0f);
            this.setAccel(0.0f);
            this.setTurnSpeed(0.0f);

            playSound(SOUND_RIDE.get(), this.getSoundVolume() * 0.15f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.1F);
        }
    }

    @Override
    public void travel(Vec3 movementInput)
    {
        isMovingBackwards = movementInput.z < 0;
        if (getCurrentState() != 0) movementInput = Vec3.ZERO;
        super.travel(new Vec3(0,0, movementInput.z));
    }

    protected void tickRidden(Player pPlayer, Vec3 pTravelVector)
    {
        super.tickRidden(pPlayer, pTravelVector);
        if (this.isControlledByLocalInstance())
        {
            float friction = this.getGroundFriction();
            if (Float.isNaN(friction)) friction = 1;
            this.setAccel(this.getAccel() * 0.95f);
            this.setTurnSpeed(Mth.clamp(this.getTurnSpeed() - pPlayer.xxa, -8, 8) * friction);
            float wheel = this.getWheelRotation() + this.getAccel() * 0.01f;
            if (wheel >= 360) wheel -= 360;
            if (wheel < 0) wheel += 360;
            this.setWheelRotation(wheel);
//            float speed = (float)this.getDeltaMovement().horizontalDistance();
//            speed = Mth.clamp(speed, 1, speed);
            float newRotation = this.getYRot() + this.getTurnSpeed() * 10;
            this.setYRot(newRotation);
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        }
    }

    protected float getRiddenSpeed(Player pPlayer) {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * Mth.abs(this.getAccel());
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Mob)
        {
            Mob mobEntity = (Mob)entity;
            return mobEntity;
        }
        if ((entity = this.getFirstPassenger()) instanceof Player)
        {
            Player playerEntity = (Player)entity;
            return playerEntity;
        }
        return null;
    }

    protected Vec3 getRiddenInput(Player pPlayer, Vec3 pTravelVector)
    {
        this.setAccel(Mth.clamp(this.getAccel() + pPlayer.zza, -10.0f, 10.0f));
        float f = pPlayer.xxa;
        float g = 0.05f * this.getAccel();
        return new Vec3(f, 0.0f, g / 10);
    }

    protected void positionRider(Entity pPassenger, MoveFunction pCallback)
    {
        super.positionRider(pPassenger, pCallback);
        float speed = this.getAccel();
        float turnSpeed = this.getTurnSpeed();
        Vec3 _pos = new Vec3(0, this.getPassengersRidingOffset(), 0);
        _pos = _pos.zRot(-turnSpeed * 0.25f);
        _pos = _pos.xRot(-speed * 0.05f);
        _pos = _pos.yRot(-this.getYRot() * Mth.DEG_TO_RAD);
        pCallback.accept(pPassenger, _pos.x() + this.getX(), _pos.y() + this.getY(), _pos.z() + this.getZ());
        if (pPassenger instanceof LivingEntity livingPassenger) livingPassenger.setYBodyRot(this.getYRot());
    }

    public double getPassengersRidingOffset() {
        return this.getBbHeight() * 1.15d;
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity pPassenger)
    {
        this.setDeployPosition(null);
        this.setWheelRotation(0.0f);
        this.setAccel(0.0f);
        this.setTurnSpeed(0.0f);
        return super.getDismountLocationForPassenger(pPassenger);
    }

    protected void clampRotation(Entity passenger) {
        passenger.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(passenger.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -90.0F, 90.0F);
        passenger.yRotO += f1 - f;
        passenger.setYRot(passenger.getYRot() + f1 - f);
        passenger.setYHeadRot(passenger.getYRot());
    }

    public void onPassengerTurned(Entity passenger) {
        this.clampRotation(passenger);
    }

    public float getGroundFriction() {
        AABB aabb = this.getBoundingBox();
        AABB aabb1 = new AABB(aabb.minX, aabb.minY - 0.001, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ);
        int i = Mth.floor(aabb1.minX) - 1;
        int j = Mth.ceil(aabb1.maxX) + 1;
        int k = Mth.floor(aabb1.minY) - 1;
        int l = Mth.ceil(aabb1.maxY) + 1;
        int i1 = Mth.floor(aabb1.minZ) - 1;
        int j1 = Mth.ceil(aabb1.maxZ) + 1;
        VoxelShape voxelshape = Shapes.create(aabb1);
        float f = 0.0F;
        int k1 = 0;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int l1 = i; l1 < j; ++l1) {
            for(int i2 = i1; i2 < j1; ++i2) {
                int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);
                if (j2 != 2) {
                    for(int k2 = k; k2 < l; ++k2) {
                        if (j2 <= 0 || k2 != k && k2 != l - 1) {
                            blockpos$mutableblockpos.set(l1, k2, i2);
                            BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
                            if (!(blockstate.getBlock() instanceof WaterlilyBlock) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level(), blockpos$mutableblockpos).move((double)l1, (double)k2, (double)i2), voxelshape, BooleanOp.AND)) {
                                f += blockstate.getBlock().getFriction();
                                ++k1;
                            }
                        }
                    }
                }
            }
        }

        return f / (float)k1;
    }

    public boolean canSpawnSprintParticle() {
        return ((!this.getPassengers().isEmpty() && Mth.abs(this.getAccel()) > 0.5f)
                || (this.getCurrentState() == 2 && this.getDandoriActivity() != DANDORI_ACTIVITIES.MINING.ordinal()))
                && this.random.nextInt(1) == 0;
    }

    private void setAccel(float newAccel)
    {
        this.entityData.set(DATA_ACCEL, newAccel);
    }
    public float getAccel()
    {
        return this.entityData.get(DATA_ACCEL);
    }

    private void setTurnSpeed(float newTurnSpeed)
    {
        this.entityData.set(DATA_TURN_SPEED, newTurnSpeed);
    }
    public float getTurnSpeed()
    {
        return this.entityData.get(DATA_TURN_SPEED);
    }

    private void setWheelRotation(float f)
    {
        this.entityData.set(DATA_WHEEL_ROTATION, f);
    }
    public float getWheelRotation()
    {
        return this.entityData.get(DATA_WHEEL_ROTATION);
    }

    // --------------------------------------------------------------------------------------------
    // Animation stuff
    private static final String RESOURCE_NAME = "golem_grindstone";

    protected String getResourcePath() {return "entity/legends/";}
    protected String getResourceName() {return RESOURCE_NAME;}

    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation." + RESOURCE_NAME + ".walk");
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation." + RESOURCE_NAME + ".idle");
    private static final RawAnimation ANIMATION_ATTACK = RawAnimation.begin().thenLoop("animation." + RESOURCE_NAME + ".attack");
    private static final RawAnimation ANIMATION_WINDUP = RawAnimation.begin().then("animation." + RESOURCE_NAME + ".attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_ATTACK_END = RawAnimation.begin().then("animation." + RESOURCE_NAME + ".attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_CARRY_FORWARD = RawAnimation.begin().thenLoop("animation." + RESOURCE_NAME + ".carry_forward");
    private static final RawAnimation ANIMATION_CARRY_IDLE = RawAnimation.begin().thenLoop("animation." + RESOURCE_NAME + ".carry_idle");
    private static final RawAnimation ANIMATION_CARRY_BACKWARD = RawAnimation.begin().thenLoop("animation." + RESOURCE_NAME + ".carry_backward");
    private static final RawAnimation ANIMATION_MINING_WINDUP = RawAnimation.begin().then("animation." + RESOURCE_NAME + ".mine_windup", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_MINING = RawAnimation.begin().then("animation." + RESOURCE_NAME + ".mine", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_MINING_END = RawAnimation.begin().then("animation." + RESOURCE_NAME + ".mine_end", Animation.LoopType.HOLD_ON_LAST_FRAME);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemGrindstone pGolem = event.getAnimatable();
            boolean hasPassengers = !pGolem.getPassengers().isEmpty();
            float speed = pGolem.getAccel();
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
                if (state == 2) return event.setAndContinue(ANIMATION_ATTACK);
                event.getController().setAnimationSpeed(1.00);
                if (state == 1) return event.setAndContinue(ANIMATION_WINDUP);
                return event.setAndContinue(ANIMATION_ATTACK_END);
            }
            if (getDeltaMovement().horizontalDistanceSqr() > 0.0001D || event.isMoving())
            {
                if (!hasPassengers)
                {
//                    event.getController().setAnimationSpeed(Mth.clamp(Mth.abs(speed) * 0.1, 0.25, Mth.abs(speed)));
//                    if (speed < 0) return event.setAndContinue(ANIMATION_CARRY_BACKWARD);
//                    return event.setAndContinue(ANIMATION_CARRY_FORWARD);
                    event.getController().setAnimationSpeed(2.00);
                    return event.setAndContinue(ANIMATION_WALK);
                }
            }
            if (hasPassengers) return event.setAndContinue(ANIMATION_CARRY_IDLE);
            return event.setAndContinue(ANIMATION_IDLE);
        }));
    }


    // =================================================================================================================
    // Audio
    protected static final Supplier<SoundEvent> SOUND_MINE = SoundRegistry.ENTITY_GOLEM_GRINDSTONE_MINE;
    protected static final Supplier<SoundEvent> SOUND_YES = SoundRegistry.ENTITY_GOLEM_GRINDSTONE_YES;
    protected static final Supplier<SoundEvent> SOUND_HIT = SoundRegistry.ENTITY_GOLEM_GRINDSTONE_HIT;
    protected static final Supplier<SoundEvent> SOUND_ORDERED = SoundRegistry.ENTITY_GOLEM_GRINDSTONE_ORDERED;
    protected static final Supplier<SoundEvent> SOUND_RIDE = SoundRegistry.ENTITY_GOLEM_GRINDSTONE_RIDE;

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
