package com.kitisplode.golemdandori2.item;

import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class ItemDandoriAttack extends AbstractDandoriTool
{
    static private final float attackRingDiameter = 6.0f;
    public ItemDandoriAttack(Properties properties)
    {
        super(properties);
    }

    protected void dandoriActPerPik(Mob mob, BlockPos targetPosition)
    {
        if (mob instanceof IEntityDandoriPik pik)
        {
            LivingEntity target = helperFindAttackTarget(mob, targetPosition, mob.level());
            if (target != null)
            {
                pik.setDandoriState(IEntityDandoriPik.DANDORI_STATES.OFF.ordinal());
                pik.setDeployPosition(null);
                mob.setTarget(helperFindAttackTarget(mob, targetPosition, mob.level()));
                pik.playSoundOrdered();
            }
        }
    }

    private LivingEntity helperFindAttackTarget(Mob mob, BlockPos targetPosition, Level level)
    {
        IEntityDandoriPik pik = (IEntityDandoriPik) mob;
        AABB box = AABB.ofSize(targetPosition.getCenter(), attackRingDiameter, attackRingDiameter, attackRingDiameter);
        List<LivingEntity> listOfEntities = helperFilterTargets(pik, level.getEntitiesOfClass(LivingEntity.class, box));
        return helperFindClosestTarget(mob, listOfEntities);
    }

    private List<LivingEntity> helperFilterTargets(IEntityDandoriPik mob, List<LivingEntity> listOfEntities)
    {
        List<LivingEntity> results = new ArrayList<>();
        for (LivingEntity entityInList : listOfEntities)
        {
            if (!(entityInList instanceof Mob)) continue;
            if (helperFilterTarget(mob, entityInList)) results.add(entityInList);
        }
        return results;
    }

    private boolean helperFilterTarget(IEntityDandoriPik mob, LivingEntity entity)
    {
        if (entity == mob.getOwner()) return false;
        if (entity instanceof Enemy) return true;
        if (entity instanceof IEntityDandoriPik pik)
        {
            LivingEntity pikOwner = pik.getOwner();
            if (pikOwner instanceof IEntityDandoriPik pikOwnerPik) return pikOwnerPik.getOwner() != mob.getOwner();
            return pik.getOwner() != mob.getOwner();
        }
        if (entity instanceof TamableAnimal animal) return animal.getOwner() != mob.getOwner();
        if (entity instanceof AbstractVillager) return false;
        return true;
    }

    private LivingEntity helperFindClosestTarget(Mob mob, List<LivingEntity> listOfEntities)
    {
        LivingEntity target = null;
        double distance = 0;
        for (LivingEntity entityInList : listOfEntities)
        {
            double distanceInList = mob.distanceToSqr(entityInList);
            if (target == null || distanceInList < distance)
            {
                target = entityInList;
                distance = distanceInList;
            }
        }
        return target;
    }
}
