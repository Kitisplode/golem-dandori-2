package com.kitisplode.golemdandori2.entity.interfaces;

import com.kitisplode.golemdandori2.util.DataDandoriCount;

public interface IEntityDandoriLeader
{
    void recountDandori();
    void setRecountDandori();

    int getTotalDandoriCount();
    int getPikCount(DataDandoriCount.FOLLOWER_TYPE type);

    void nextDandoriCurrentType();
    DataDandoriCount.FOLLOWER_TYPE getDandoriCurrentType();
}
