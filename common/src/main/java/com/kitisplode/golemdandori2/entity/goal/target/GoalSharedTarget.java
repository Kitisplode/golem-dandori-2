package com.kitisplode.golemdandori2.entity.goal.target;

import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GoalSharedTarget<R extends Mob, T extends LivingEntity> extends NearestAttackableTargetGoalBiggerY<T>
{
    private final Class<R> sharedClass;
    private final IEntityDandoriPik pik;

    public GoalSharedTarget(Mob pMob, Class<R> pSharedClass, Class<T> pClass, int pRandomInterval, boolean pMustSee, boolean pMustReach, TargetingConditions.@Nullable Selector pTargetPredicate, float pYRange)
    {
        super(pMob, pClass, pRandomInterval, pMustSee, pMustReach, pTargetPredicate, pYRange);
        assert(mob instanceof IEntityDandoriPik);
        this.pik = (IEntityDandoriPik) mob;
        this.sharedClass = pSharedClass;
    }

    protected void findTarget()
    {
        if (this.targetType == Player.class || this.targetType == ServerPlayer.class)
        {
            this.target = this.mob.level().getNearestPlayer(this.mob, this.getFollowDistance());
            return;
        }
        ServerLevel serverLevel = getServerLevel(this.mob);
        ArrayList<EntityDistanceNode> nodeList = new ArrayList<>();
        List<T> entityList = this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), livingEntity -> true);
        for (T entity : entityList)
        {
            if (!this.targetConditions.test(serverLevel, this.mob, entity)) continue;
            double d = this.mob.getEyePosition().distanceToSqr(entity.position());
            int i = 0;
            for (; i < nodeList.size(); i++)
                if (d < nodeList.get(i).distance) break;
            nodeList.add(i, new EntityDistanceNode(entity, d));
        }
        for (EntityDistanceNode node : nodeList)
        {
            List<R> sharedEntityList = this.mob.level().getEntitiesOfClass(this.sharedClass,
                    this.getTargetSearchArea(this.getFollowDistance()),
                    entity -> entity instanceof IEntityDandoriPik entityDandoriPik
                        && entity.getTarget() == node.entity
                        && entityDandoriPik.getOwner() == this.pik.getOwner());
            if (sharedEntityList.isEmpty())
            {
                this.target = node.entity;
                return;
            }
        }
        if (!nodeList.isEmpty())
        {
            this.target = nodeList.get(0).entity;
        }
    }

    class EntityDistanceNode
    {
        public final T entity;
        public final double distance;
        public EntityDistanceNode(T entity, double distance)
        {
            this.entity = entity;
            this.distance = distance;
        }
    }
}
