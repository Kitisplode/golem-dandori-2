package com.kitisplode.golemdandori2.entity.goal.target;

import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class NearestAttackableTargetGoalBiggerY<T extends LivingEntity> extends NearestAttackableTargetGoal<T>
{
    private final float yRange;

    public NearestAttackableTargetGoalBiggerY(Mob pMob, Class pClass, int pRandomInterval, boolean pMustSee, boolean pMustReach, @Nullable TargetingConditions.Selector pTargetPredicate, float pYRange)
    {
        super(pMob, pClass, pRandomInterval, pMustSee, pMustReach, pTargetPredicate);
        yRange = pYRange;
    }

    protected @NotNull AABB getTargetSearchArea(double pTargetDistance)
    {
        return this.mob.getBoundingBox().inflate(pTargetDistance, yRange, pTargetDistance);
    }

    protected double getFollowDistance()
    {
        if (this.mob instanceof IEntityDandoriPik) return ((IEntityDandoriPik)this.mob).getTargetRange();
        return this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);
    }
}
