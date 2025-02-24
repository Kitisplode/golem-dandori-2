package com.kitisplode.golemdandori2.entity.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

public interface IEntityDandoriPik
{byte ENTITY_EVENT_DANDORI_START = 8;

    void playSoundYes();
    void playSoundOrdered();

    enum DANDORI_STATES
    {
        OFF,
        SOFT,
        HARD
    }

    enum DANDORI_ACTIVITIES
    {
        IDLE,
        DEPLOY,
        COMBAT,
        MINING,
        HAULING
    }

    public int getDandoriState();

    public void setDandoriState(int pDandoriState);

    default boolean isDandoriOff()
    {
        return getDandoriState() == DANDORI_STATES.OFF.ordinal();
    }
    default boolean isDandoriOn()
    {
        return getDandoriState() != DANDORI_STATES.OFF.ordinal();
    }
    default boolean isDandoriSoft()
    {
        return getDandoriState() == DANDORI_STATES.SOFT.ordinal();
    }
    default boolean isDandoriHard()
    {
        return getDandoriState() == DANDORI_STATES.HARD.ordinal();
    }

    public int getDandoriActivity();
    public void setDandoriActivity(int pDandoriActivity);

    LivingEntity getOwner();
    void setOwner(LivingEntity newOwner);
    public boolean isImmobile();
    default boolean isThrowable()
    {
        return false;
    }
    default boolean getThrown()
    {
        return false;
    }
    default void setThrown(boolean pThrown)
    {
        return;
    }
    default float getThrowAngle()
    {
        return 0.0f;
    }

    boolean isIdle();
    void setDeployPosition(BlockPos bp);
    BlockPos getDeployPosition();

    double getTargetRange();
}
