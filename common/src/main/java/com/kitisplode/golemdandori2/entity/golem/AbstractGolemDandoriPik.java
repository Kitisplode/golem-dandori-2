package com.kitisplode.golemdandori2.entity.golem;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.entity.goal.action.GoalDandoriFollowHard;
import com.kitisplode.golemdandori2.entity.goal.action.GoalMoveToDeployPosition;
import com.kitisplode.golemdandori2.entity.goal.target.GoalSharedTarget;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityWithMultiStageAttack;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityWithMultiStageMine;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

//import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

abstract public class AbstractGolemDandoriPik extends AbstractGolem implements GeoEntity, IEntityDandoriPik, IEntityWithMultiStageAttack, IEntityWithMultiStageMine
{
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(AbstractGolemDandoriPik.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> DATA_CURRENT_STATE = SynchedEntityData.defineId(AbstractGolemDandoriPik.class, EntityDataSerializers.INT);


    protected BlockPos deployPosition = null;
    protected int dandoriState = DANDORI_STATES.OFF.ordinal();
    protected int dandoriActivity = DANDORI_ACTIVITIES.IDLE.ordinal();

    protected BlockPos minePosition = null;
    protected int mineProgress;
    protected int mineProgressPrevious;
    protected Block mineBlockType = null;

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

//    @Nullable
    public UUID getOwnerUUID() {
        return (UUID)((Optional)this.entityData.get(DATA_OWNERUUID_ID)).orElse((Object)null);
    }

    public void setOwnerUUID( UUID uuid) {
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
        
        // Move to deploy position
        this.goalSelector.addGoal(20, new GoalMoveToDeployPosition(this, 3.0,1.0, DANDORI_ACTIVITIES.DEPLOY));
        // Return to patrol deploy if wandered too far.
        this.goalSelector.addGoal(30, new GoalMoveToDeployPosition(this, 16.0,1.0, DANDORI_ACTIVITIES.IDLE));

        // Child entities can put goals here between 30 and 80 for things that are lower priority than deploying, but still important to do while idle.

        // Wander around deploy position.
        this.goalSelector.addGoal(90, new RandomLookAroundGoal(this));

        // Target goals
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
//        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Mob.class, 5, false, false, (p_28879_, p_376412_) -> {
//            return p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper);
//        }));
        this.targetSelector.addGoal(3, new GoalSharedTarget<>(this, AbstractGolemDandoriPik.class, Mob.class, 5, true, false, (p_28879_, p_376412_) -> p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper),5));
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.getMinePosition() != null && this.getDandoriActivity() != DANDORI_ACTIVITIES.MINING.ordinal())
        {
            this.setMinePosition(null);
        }
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
    public int getDandoriActivity()
    {
        return dandoriActivity;
    }

    @Override
    public void setDandoriActivity(int pDandoriActivity)
    {
        dandoriActivity = pDandoriActivity;
    }

    @Override
    public boolean isIdle()
    {
        return this.dandoriActivity == DANDORI_ACTIVITIES.IDLE.ordinal();
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

    // =================================================================================================================
    // Mining behavior

    @Override
    public boolean isReadyToMine()
    {
        return this.dandoriActivity == DANDORI_ACTIVITIES.MINING.ordinal();
    }

    @Override
    public void setMinePosition(BlockPos bp)
    {
        minePosition = bp;
    }

    @Override
    public BlockPos getMinePosition()
    {
        return minePosition;
    }

    @Override
    public void setMineProgress(int mineProgress)
    {
        this.mineProgress = mineProgress;
        this.mineProgressPrevious = mineProgress;
    }

    protected void helperMineBlock(BlockPos bp)
    {
        if (bp == null) return;

        if (!this.level().getBlockState(bp).isAir())
        {
            this.mineBlockType = this.level().getBlockState(bp).getBlock();
            // Mine the block!
            this.mineProgress += 11;
            int i = (int) ((float) this.mineProgress / 10.0f);
            if (i != this.mineProgressPrevious)
            {
                this.level().destroyBlockProgress(this.getId(), bp, i);
                this.mineProgressPrevious = i;
            }

            // If we're done mining the block, destroy the block.
            if (this.mineProgress >= 100.0f)
            {
                this.level().destroyBlock(bp, true);
                this.mineProgress = 0;
                this.mineProgressPrevious = 0;
                this.level().levelEvent(2001, bp, Block.getId(this.level().getBlockState(bp)));
            }
        }
        else
        {
            this.minePosition = helperFindNextMinePosition(this.getOnPos().above(2), bp, this.mineBlockType);
            // If we couldn't find any new blocks to mine, just stop mining.
            if (this.minePosition == null)
            {
                this.dandoriActivity = DANDORI_ACTIVITIES.IDLE.ordinal();
            }
            // Also if the mine position we find is too far away from the deploy position, then abort mining.
            else if (this.deployPosition != null && this.deployPosition.distToCenterSqr(this.minePosition.getCenter()) > 20*20)
            {
                this.minePosition = null;
                this.dandoriActivity = DANDORI_ACTIVITIES.IDLE.ordinal();
            }
        }
    }

    protected BlockPos helperFindNextMinePosition(BlockPos mobPosition, BlockPos bp, Block block)
    {
        if (block == null) return null;
        List<BlockPos> bpList = helperGetBlockPosListInArea(new AABB(mobPosition).inflate(8,1,8), this.level());
        List<BlockPos> bpMatchingList = helperFindMatchingBlocks(bpList, block, this.level());
        return helperFindClosestBlock(bpMatchingList, mobPosition, bp, this.level().getRandom());
    }

    protected boolean helperAreBlocksMatching(Block bs1, Block bs2)
    {
        return bs1.equals(bs2);
    }

    protected List<BlockPos> helperGetBlockPosListInArea(AABB aabb, Level level)
    {
        List<BlockPos> results = new ArrayList<>();
        for(double x = aabb.minX; x <= aabb.maxX; x++)
        {
            for (double y = aabb.minY; y <= aabb.maxY; y++)
            {
                for (double z = aabb.minZ; z <= aabb.maxZ; z++)
                {
                    results.add(new BlockPos((int)x, (int)y, (int)z));
                }
            }
        }
        return results;
    }

    protected List<BlockPos> helperFindMatchingBlocks(List<BlockPos> blockPosList, Block block, Level level)
    {
        List<BlockPos> results = new ArrayList<>();
        if (blockPosList == null) return results;
        for (BlockPos bpInList : blockPosList)
        {
            BlockState bsInList = level.getBlockState(bpInList);
            if (helperAreBlocksMatching(block, bsInList.getBlock()))
            {
                results.add(bpInList);
            }
        }
        return results;
    }

    protected BlockPos helperFindClosestBlock(List<BlockPos> blockPosList, BlockPos bp, BlockPos previousPos, RandomSource random)
    {
        BlockPos result = null;
        double resultDistance = 0;
        for (BlockPos bpInList : blockPosList)
        {
            double distanceInList = bp.distToCenterSqr(bpInList.getCenter());
            if (previousPos.equals(bpInList)) continue;
            if (result == null || (distanceInList < resultDistance && random.nextFloat() <= 0.75f))
            {
                result = bpInList;
                resultDistance = distanceInList;
            }
        }
        return result;
    }
}
