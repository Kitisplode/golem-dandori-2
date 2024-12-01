package com.kitisplode.golemdandori2.item;

import net.minecraft.world.entity.player.Player;

public interface IItemSwingUse
{
    default void swing(Player player) {}

    default void swingTick(Player player) {}
}
