package com.kitisplode.golemdandori2.item;

import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityWithMultiStageMine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

public class ItemDandoriDig extends AbstractDandoriTool
{
    public ItemDandoriDig(Properties properties)
    {
        super(properties);
    }

    protected void dandoriActPerPik(Mob mob, BlockPos targetPosition)
    {
        if (mob instanceof IEntityDandoriPik pik)
        {
            pik.setDandoriState(IEntityDandoriPik.DANDORI_STATES.OFF.ordinal());
            pik.setDeployPosition(targetPosition);
            if (pik instanceof IEntityWithMultiStageMine dandoriMiner && dandoriMiner.canMine())
            {
                pik.setDandoriActivity(IEntityDandoriPik.DANDORI_ACTIVITIES.MINING.ordinal());
                dandoriMiner.setMinePosition(targetPosition);
                pik.playSoundOrdered();
            }
        }
    }
}
