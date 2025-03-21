package com.kitisplode.golemdandori2.entity.goal.action;

import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GoalMoveToDeployPosition extends Goal
{
    private final IEntityDandoriPik dandoriFollower;
    private final Mob mob;
    private final double proximityDistance;
    private final double speed;
    private final IEntityDandoriPik.DANDORI_ACTIVITIES activityType;

    public GoalMoveToDeployPosition(Mob mob, double proximityDistance, double speed, IEntityDandoriPik.DANDORI_ACTIVITIES activityType)
    {
        this.mob = mob;
        assert(mob instanceof IEntityDandoriPik);
        this.dandoriFollower = (IEntityDandoriPik) mob;
        this.proximityDistance = proximityDistance;
        this.speed = speed;
        this.activityType = activityType;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        if (this.dandoriFollower.getDandoriActivity() != this.activityType.ordinal()) return false;
        BlockPos bp = this.dandoriFollower.getDeployPosition();
        return bp != null && this.isTooFarFrom(bp, this.proximityDistance);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.dandoriFollower.setDandoriActivity(IEntityDandoriPik.DANDORI_ACTIVITIES.IDLE.ordinal());
    }

    @Override
    public void tick() {
        BlockPos blockPos = this.dandoriFollower.getDeployPosition();
        if (blockPos != null && this.mob.getNavigation().isDone()) {
            if (this.isTooFarFrom(blockPos, 10.0)) {
                Vec3 vec3d = new Vec3((double)blockPos.getX() - this.mob.getX(), (double)blockPos.getY() - this.mob.getY(), (double)blockPos.getZ() - this.mob.getZ()).normalize();
                Vec3 vec3d2 = vec3d.scale(10.0).add(this.mob.getX(), this.mob.getY(), this.mob.getZ());
                this.mob.getNavigation().moveTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            } else {
                this.mob.getNavigation().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.speed * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            }
        }
    }

    private boolean isTooFarFrom(BlockPos pos, double proximityDistance)
    {
        return !pos.closerToCenterThan(this.mob.position(), proximityDistance);
    }
}
