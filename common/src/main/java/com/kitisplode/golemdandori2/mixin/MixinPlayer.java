package com.kitisplode.golemdandori2.mixin;

import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriLeader;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import com.kitisplode.golemdandori2.util.DataDandoriCount;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = Player.class)
public abstract class MixinPlayer extends LivingEntity implements IEntityDandoriLeader
{
    private static final EntityDataAccessor<Integer> DANDORI_TOTAL = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);

    private static Map<DataDandoriCount.FOLLOWER_TYPE, EntityDataAccessor<Integer>> DANDORI_COUNTS = new HashMap<>();

    static
    {
        for (DataDandoriCount.FOLLOWER_TYPE type : DataDandoriCount.FOLLOWER_TYPE.values())
        {
            DANDORI_COUNTS.put(type, SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT));
        }
    }

    private DataDandoriCount dandoriCount = new DataDandoriCount();
    private boolean recountDandori = false;
    private DataDandoriCount.FOLLOWER_TYPE currentType = null;

    protected MixinPlayer(EntityType<? extends LivingEntity> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    @Inject(method = "defineSynchedData", at = @At("tail"))
    protected void inject_initDefineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci)
    {
        builder.define(DANDORI_TOTAL, 0);
        for (DataDandoriCount.FOLLOWER_TYPE type : DataDandoriCount.FOLLOWER_TYPE.values())
        {
            builder.define(DANDORI_COUNTS.get(type), 0);
        }
    }

    @Override
    public void push(Entity entity)
    {
        if (entity instanceof IEntityDandoriPik && ((IEntityDandoriPik) entity).getOwner() == this) return;
        super.push(entity);
    }

    @Inject (method = "tick", at = @At("tail"))
    protected void inject_tick(CallbackInfo ci)
    {
        recountDandori();
    }

    // =================================================================================================================
    // Dandori counting

    public void recountDandori()
    {
        if (!recountDandori) return;
        dandoriCount.updateCounts(this);
        this.entityData.set(DANDORI_TOTAL, dandoriCount.getTotalCount());
        for (DataDandoriCount.FOLLOWER_TYPE type : DataDandoriCount.FOLLOWER_TYPE.values())
        {
            this.entityData.set(DANDORI_COUNTS.get(type), dandoriCount.getFollowerCount(type));
        }
        recountDandori = false;
    }

    public void setRecountDandori()
    {
        recountDandori = true;
    }

    public int getTotalDandoriCount()
    {
        return this.entityData.get(DANDORI_TOTAL);
    }

    public int getPikCount(DataDandoriCount.FOLLOWER_TYPE type)
    {
        return this.entityData.get(DANDORI_COUNTS.get(type));
    }

    public void nextDandoriCurrentType()
    {

    }
    public DataDandoriCount.FOLLOWER_TYPE getDandoriCurrentType()
    {
        return currentType;
    }
}
