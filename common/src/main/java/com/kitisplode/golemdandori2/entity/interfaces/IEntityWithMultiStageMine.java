package com.kitisplode.golemdandori2.entity.interfaces;

import net.minecraft.core.BlockPos;

public interface IEntityWithMultiStageMine
{
    int getCurrentState();
    void setCurrentState(int pInt);
    boolean tryMine();

    boolean isReadyToMine();

    void setMinePosition(BlockPos bp);
    BlockPos getMinePosition();

    void setMineProgress(int mineProgress);

    boolean canMine();

}
