package com.kitisplode.golemdandori2.entity.golem.legends;

import com.kitisplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import com.kitisplode.golemdandori2.registry.SoundRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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

public class EntityGolemGrindstone extends AbstractGolemDandoriPik
{
    private boolean isMovingBackwards = false;

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

    @Override
    public boolean tryAct()
    {
        return false;
    }

    @Override
    public boolean tryMine()
    {
        return false;
    }

    @Override
    public boolean canMine()
    {
        return false;
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
            this.setDandoriState(DANDORI_STATES.OFF.ordinal());

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
            float newRotation = this.getYRot() - pPlayer.xxa * 10;
            this.setRot(newRotation, 0.0f);
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        }
    }

    protected float getRiddenSpeed(Player pPlayer) {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
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
        float f = pPlayer.xxa;
        float g = pPlayer.zza * 0.5f;
        return new Vec3(f, 0.0f, g);
    }

    protected void positionRider(Entity pPassenger, MoveFunction pCallback)
    {
        super.positionRider(pPassenger, pCallback);
        pCallback.accept(pPassenger, this.getX(), this.getY() + this.getPassengersRidingOffset(), this.getZ());
        if (pPassenger instanceof LivingEntity livingPassenger) livingPassenger.setYBodyRot(this.getYRot());
    }

    public double getPassengersRidingOffset() {
        return this.getBbHeight() * 1.25d;
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity pPassenger)
    {
        this.setDeployPosition(null);
        return super.getDismountLocationForPassenger(pPassenger);
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemGrindstone pGolem = event.getAnimatable();
            boolean hasPassengers = !pGolem.getPassengers().isEmpty();
            int state = pGolem.getCurrentState();
            if (state > 0)
            {
                if (state == 1)
                {
                    event.getController().setAnimationSpeed(1.00);
                    return event.setAndContinue(ANIMATION_WINDUP);
                }
                else if (state == 2)
                {
                    event.getController().setAnimationSpeed(2.00);
                    return event.setAndContinue(ANIMATION_ATTACK);
                }
                else
                {
                    event.getController().setAnimationSpeed(1.00);
                    return event.setAndContinue(ANIMATION_ATTACK_END);
                }
            }
            else
            {
                event.getController().setAnimationSpeed(1.00);
                if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                {
                    if (hasPassengers)
                    {
                        if (pGolem.isMovingBackwards)
                            return event.setAndContinue(ANIMATION_CARRY_BACKWARD);
                        return event.setAndContinue(ANIMATION_CARRY_FORWARD);
                    }
                    return event.setAndContinue(ANIMATION_WALK);
                }
            }
            if (hasPassengers)
            {
                return event.setAndContinue(ANIMATION_CARRY_IDLE);
            }
            return event.setAndContinue(ANIMATION_IDLE);
        }));
    }


    // =================================================================================================================
    // Audio
    protected static final Supplier<SoundEvent> SOUND_RIDE = SoundRegistry.ENTITY_GOLEM_GRINDSTONE_RIDE;
    protected static final Supplier<SoundEvent> SOUND_YES = SoundRegistry.ENTITY_GOLEM_COBBLE_YES;
    protected static final Supplier<SoundEvent> SOUND_ACT = SoundRegistry.ENTITY_GOLEM_COBBLE_ARMS;
    protected static final Supplier<SoundEvent> SOUND_ORDERED = SoundRegistry.ENTITY_GOLEM_COBBLE_ORDERED;

    @Override
    public void playSoundYes()
    {

    }

    @Override
    public void playSoundOrdered()
    {

    }
}
