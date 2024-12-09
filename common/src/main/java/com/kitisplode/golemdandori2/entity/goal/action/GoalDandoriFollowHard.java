package com.kitisplode.golemdandori2.entity.goal.action;

import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GoalDandoriFollowHard extends Goal
{
    protected final PathfinderMob mob;
    private final IEntityDandoriPik pik;
    protected LivingEntity owner;

    private final double speedModifier;
    private final double stopRange;

    private boolean isRunning;

    public GoalDandoriFollowHard(PathfinderMob pMob, double pSpeed, double pRange)
    {
        this.mob = pMob;
        this.pik = (IEntityDandoriPik) pMob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.speedModifier = pSpeed;
        this.stopRange = Mth.square(pRange);
    }

    @Override
    public boolean canUse()
    {
        if (this.mob.isSleeping()) return false;
        if (this.pik.isImmobile()) return false;
        if (!this.pik.isDandoriHard()) return false;
        // TODO: If the entity is currently performing an attack (or similar) action, do not.
        // If the pik has an owner, they can follow them.
        this.owner = this.pik.getOwner();
        return this.owner != null;
    }

//    @Override
//    public boolean canContinueToUse()
//    {
//        if (!this.canUse()) return false;
//        return this.mob.getTarget() != null;
//    }

    public void start()
    {
        this.isRunning = true;
    }

    public void stop()
    {
        this.isRunning = false;
        this.owner = null;
        this.mob.getNavigation().stop();
    }

    public void tick()
    {
        if (this.owner == null) return;
        this.mob.getLookControl().setLookAt(this.owner);
        // If we're too close to the owner, then stop moving.
        if (this.mob.distanceToSqr(this.owner) < stopRange)
            this.mob.getNavigation().stop();
        // Otherwise, get closer to the owner.
        else
            this.mob.getNavigation().moveTo(this.owner, this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
    }

    public boolean isRunning()
    {
        return this.isRunning;
    }
}
