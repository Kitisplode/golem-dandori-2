package com.kitisplode.golemdandori2.entity.goal.action;

import com.kitisplode.golemdandori2.entity.interfaces.IEntityWithMultiStageAttack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class GoalMultiStageAttack extends MeleeAttackGoal
{
    private long targetOutVisionTimer;
    private int targetOutVisionTime = 20 * 5;

    private final IEntityWithMultiStageAttack attacker;
    private final int[] stages;
    private final int turnDuringState;
    private final double attackRange;
    private final double speed;

    private final int startingState;

    private Path path;
    private long lastUpdateTime;
    private Double targetX;
    private Double targetY;
    private Double targetZ;

    private int currentState = 0;
    private int timer = 0;

    private boolean forced = false;
    private boolean running = false;
    private int cooldown = 0;
    private int cooldownMax;

    public GoalMultiStageAttack(IEntityWithMultiStageAttack mob, double speedModifier, boolean followingTargetEvenIfNotSeen, double pAttackRange, int pStartingState, int[] pStages, int pTurnDuringState)
    {
        super((PathfinderMob) mob, speedModifier, followingTargetEvenIfNotSeen);
        attacker = mob;
        speed = speedModifier;
        attackRange = pAttackRange;
        startingState = pStartingState;
        stages = pStages.clone();
        turnDuringState = pTurnDuringState;
    }

    public GoalMultiStageAttack(IEntityWithMultiStageAttack mob, double speedModifier, boolean followingTargetEvenIfNotSeen, double pAttackRange, int pStartingState, int[] pStages)
    {
        this(mob, speedModifier, followingTargetEvenIfNotSeen, pAttackRange, pStartingState, pStages, 0);
    }

    public boolean canUse()
    {
        if (this.cooldown > 0) this.cooldown--;
        if (!this.isCooledDown()) return false;
        if (forced) return true;

        long i = this.mob.level().getGameTime();
        if (i - this.lastUpdateTime < 20L) return false;
        this.lastUpdateTime = i;

        LivingEntity target = this.mob.getTarget();
        if (target == null) return false;
        if (!target.isAlive()) return false;
        this.path = this.mob.getNavigation().createPath(target, 0);
        if (this.path != null) return true;

        Vec3 distanceFlattened = new Vec3(target.getX() - this.mob.getX(), 0, target.getZ() - this.mob.getZ());
        double distanceFlatSquared = distanceFlattened.lengthSqr();
        return this.getAttackReachSqr(target) >= distanceFlatSquared;
    }

    public boolean canContinueToUse()
    {
        if (forced) return true;
        if (currentState > 0) return true;
        if (targetOutVisionTimer >= targetOutVisionTime) return false;
        return super.canContinueToUse();
    }

    public void start()
    {
        super.start();
        reset();
        if (forced) timer = adjustedTickDelay(stages[0]);
    }

    public void stop()
    {
        super.stop();
        reset();
        forced = false;
    }

    public boolean requiresUpdateEveryTick()
    {
        return true;
    }

    public void tick()
    {
        LivingEntity target = this.mob.getTarget();
        if (timer <= 0)
        {
            if (forced) forced = false;
            if (target == null) return;

            boolean canSeeTarget = this.mob.hasLineOfSight(target);
            if (!canSeeTarget) targetOutVisionTimer++;
            else targetOutVisionTimer = 0;

            double distanceToTarget = this.mob.distanceToSqr(target);
            if (distanceToTarget > attackRange || (distanceToTarget > 3.0d && !canSeeTarget))
            {
                if (targetX == null || targetY == null || targetZ == null || target.distanceToSqr(targetX, targetY, targetZ) >= 1.0)
                {
                    targetX = target.getX();
                    targetY = target.getY();
                    targetZ = target.getZ();
                    mob.getNavigation().moveTo(target, speed * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                }
            }
            else
            {
                mob.getNavigation().stop();
                targetX = null;
                targetY = null;
                targetZ = null;
                timer = adjustedTickDelay(stages[0]);
                resetCooldown();
            }
        }
        else
        {
            timer--;
        }
        if (currentState <= turnDuringState && target != null)
        {
            this.mob.getLookControl().setLookAt(target, 30,30);
//            turnTowardsTarget(target);
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
        attacker.tryAct();
    }

    protected double getAttackReachSqr(LivingEntity pAttackTarget)
    {
        if (attackRange <= 9) return (this.mob.getBbWidth() * 2.0f * this.mob.getBbWidth() * 2.0f + pAttackTarget.getBbWidth());
        return attackRange;
    }

    public void forceAttack()
    {
        forced = true;
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
        targetOutVisionTimer = 0;
        timer = 0;
        attacker.setCurrentState(0);
        targetX = null;
        targetY = null;
        targetZ = null;
    }
}
