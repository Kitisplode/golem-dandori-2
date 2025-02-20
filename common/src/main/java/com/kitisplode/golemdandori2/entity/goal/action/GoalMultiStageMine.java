package com.kitisplode.golemdandori2.entity.goal.action;

import com.kitisplode.golemdandori2.entity.interfaces.IEntityWithMultiStageMine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class GoalMultiStageMine extends MeleeAttackGoal
{
    private final IEntityWithMultiStageMine attacker;
    private final int[] stages;
    private final int turnDuringState;
    private final double attackRange;
    private final double speed;

    private final int startingState;

    private Path path;
    private long lastUpdateTime;

    private int currentState = 0;
    private int timer = 0;

    private boolean running = false;
    private int cooldown = 0;
    private int cooldownMax;

    public GoalMultiStageMine(IEntityWithMultiStageMine mob, double speedModifier, boolean followingTargetEvenIfNotSeen, double pAttackRange, int pStartingState, int[] pStages, int pTurnDuringState)
    {
        super((PathfinderMob) mob, speedModifier, followingTargetEvenIfNotSeen);
        attacker = mob;
        speed = speedModifier;
        attackRange = pAttackRange;
        startingState = pStartingState;
        stages = pStages.clone();
        turnDuringState = pTurnDuringState;
    }

    public GoalMultiStageMine(IEntityWithMultiStageMine mob, double speedModifier, boolean followingTargetEvenIfNotSeen, double pAttackRange, int pStartingState, int[] pStages)
    {
        this(mob, speedModifier, followingTargetEvenIfNotSeen, pAttackRange, pStartingState, pStages, 0);
    }

    public boolean canUse()
    {
        if (this.cooldown > 0) this.cooldown--;
        if (!this.isCooledDown()) return false;

        long i = this.mob.level().getGameTime();
        if (i - this.lastUpdateTime < 20L) return false;
        this.lastUpdateTime = i;

        if (!this.attacker.isReadyToMine()) return false;

        BlockPos targetPos = this.attacker.getMinePosition();
        if (targetPos == null) return false;

        this.path = this.mob.getNavigation().createPath(targetPos, 0);
        if (this.path != null) return true;

//        LivingEntity target = this.mob.getTarget();
//        if (target == null) return false;
//        if (!target.isAlive()) return false;
//        this.path = this.mob.getNavigation().createPath(target, 0);
//        if (this.path != null) return true;

        Vec3 distanceFlattened = new Vec3(targetPos.getX() - this.mob.getX(), 0, targetPos.getZ() - this.mob.getZ());
        double distanceFlatSquared = distanceFlattened.lengthSqr();
        return this.getAttackReachSqr() >= distanceFlatSquared;
    }

    public boolean canContinueToUse()
    {
        if (currentState > 0) return true;
        if (!this.attacker.isReadyToMine()) return false;
        if (this.attacker.getMinePosition() == null) return false;

        return true;
    }

    public void start()
    {
        super.start();
        reset();
    }

    public void stop()
    {
        super.stop();
        reset();
    }

    public boolean requiresUpdateEveryTick()
    {
        return true;
    }

    public void tick()
    {
        BlockPos targetPos = this.attacker.getMinePosition();
        if (timer <= 0)
        {
            if (targetPos == null) return;

            double distanceToTarget = this.mob.distanceToSqr(targetPos.getCenter());
            if (distanceToTarget > attackRange)
            {
                this.mob.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), speed*this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            }
            else
            {
                mob.getNavigation().stop();
                timer = adjustedTickDelay(stages[0]);
                resetCooldown();
            }
        }
        else
        {
            timer--;
        }
        if (currentState <= turnDuringState && targetPos != null)
        {
            this.mob.getLookControl().setLookAt(targetPos.getCenter());
        }
        int previousState = currentState;
        currentState = calculateCurrentState(timer);
        attacker.setCurrentState(currentState);
        if (previousState != currentState) act();
    }

    private int calculateCurrentState(int pTimer)
    {
        if (pTimer <= 0) return startingState;
        for (int i = 1; i < stages.length; i++)
        {
            if (currentState > i) continue;
            if (pTimer >= stages[i]) return i;
        }
        return stages.length + startingState;
    }

    private void act()
    {
        attacker.tryMine();
    }

    protected double getAttackReachSqr()
    {
        return attackRange;
    }

    public boolean isRunning()
    {
        return running;
    }

    protected void resetCooldown()
    {
        this.cooldown = this.adjustedTickDelay(this.cooldownMax);
    }

    public boolean isCooledDown()
    {
        return this.cooldown <= 0;
    }
    protected int getCooldown()
    {
        return this.cooldown;
    }

    protected int getMaxCooldown()
    {
        return this.adjustedTickDelay(20);
    }

    protected void reset()
    {
        timer = 0;
        attacker.setCurrentState(0);
    }
}
