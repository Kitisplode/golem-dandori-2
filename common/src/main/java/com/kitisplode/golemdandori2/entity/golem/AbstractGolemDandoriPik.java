package com.kitisplode.golemdandori2.entity.golem;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.entity.goal.action.GoalDandoriFollowHard;
import com.kitisplode.golemdandori2.entity.goal.action.GoalMoveToDeployPosition;
import com.kitisplode.golemdandori2.entity.goal.target.GoalSharedTarget;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityWithMultiStageAttack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

abstract public class AbstractGolemDandoriPik extends AbstractGolem implements GeoEntity, IEntityDandoriPik, IEntityWithMultiStageAttack
{
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(AbstractGolemDandoriPik.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> DATA_CURRENT_STATE = SynchedEntityData.defineId(AbstractGolemDandoriPik.class, EntityDataSerializers.INT);

    protected BlockPos deployPosition = null;
    protected int dandoriState = DANDORI_STATES.OFF.ordinal();

    protected AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);


    protected AbstractGolemDandoriPik(EntityType<? extends AbstractGolem> entityType, Level level)
    {
        super(entityType, level);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
        builder.define(DATA_CURRENT_STATE, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getOwnerUUID() != null) {
            compound.putUUID("Owner", this.getOwnerUUID());
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        UUID uuid;
        if (compound.hasUUID("Owner")) {
            uuid = compound.getUUID("Owner");
        } else {
            String s = compound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
            } catch (Throwable var4) {
            }
        }
    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID)((Optional)this.entityData.get(DATA_OWNERUUID_ID)).orElse((Object)null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }

    public int getCurrentState()
    {
        return this.entityData.get(DATA_CURRENT_STATE);
    }

    public void setCurrentState(int pInt)
    {
        this.entityData.set(DATA_CURRENT_STATE, pInt);
    }

    // =================================================================================================================
    // Behavior
    @Override
    protected void registerGoals()
    {
        // Hard follow
        this.goalSelector.addGoal(0, new GoalDandoriFollowHard(this, 1.1, 6.0));

        // Move to and attack target goals can be put in by child entities here between 0 and 20.
        
        // Move to patrol position
        this.goalSelector.addGoal(20, new GoalMoveToDeployPosition(this, 2.0,1.0));
        // Wander around patrol position

        // Idle goals
        this.goalSelector.addGoal(100, new RandomLookAroundGoal(this));

        // Target goals
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
//        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Mob.class, 5, false, false, (p_28879_, p_376412_) -> {
//            return p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper);
//        }));
        this.targetSelector.addGoal(3, new GoalSharedTarget<>(this, AbstractGolemDandoriPik.class, Mob.class, 5, true, false, (p_28879_, p_376412_) -> p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper),5));
    }

    // =================================================================================================================
    // Animation

    protected abstract String getResourcePath();
    protected abstract String getResourceName();

    public ResourceLocation getModel()
    {
        return ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "geo/" + getResourcePath() + getResourceName() + ".geo.json");
    }

    public ResourceLocation getTexture()
    {
        return ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "textures/" + getResourcePath() + getResourceName() + ".png");
    }

    public ResourceLocation getAnimations()
    {
        return ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "animations/" + getResourcePath() + getResourceName() + ".animation.json");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    // =================================================================================================================
    // Dandori behaviors

    @Override
    public int getDandoriState()
    {
        return dandoriState;
    }

    @Override
    public void setDandoriState(int pDandoriState)
    {
        dandoriState = pDandoriState;
    }

    @Override
    public LivingEntity getOwner()
    {
        UUID uUID = getOwnerUUID();
        if (uUID == null) return null;
        return this.level().getPlayerByUUID(uUID);
    }

    @Override
    public void setOwner(LivingEntity pOwner)
    {
        if (pOwner != null) setOwnerUUID(pOwner.getUUID());
    }

    @Override
    public boolean isImmobile()
    {
        return false;
    }

    @Override
    public void setDeployPosition(BlockPos bp)
    {
        deployPosition = bp;
    }

    @Override
    public BlockPos getDeployPosition()
    {
        return deployPosition;
    }

    @Override
    public double getTargetRange()
    {
        if (this.isDandoriOn()) return 6;
        return this.getAttributeValue(Attributes.FOLLOW_RANGE);
    }
}
