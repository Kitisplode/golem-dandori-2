package com.kitisplode.golemdandori2.entity.interfaces;

public interface IEntityWithMultiStageAttack
{
    int getCurrentState();
    void setCurrentState(int pInt);
    boolean tryAct();
}
