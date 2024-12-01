package com.kitisplode.golemdandori2.entity.golem.legends;

import com.kitisplode.golemdandori2.entity.goal.action.GoalDandoriFollowHard;
import com.kitisplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

public class EntityGolemCobble extends AbstractGolemDandoriPik
{
    public EntityGolemCobble(EntityType<? extends AbstractGolem> entityType, Level level)
    {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.MAX_HEALTH, 50.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.35f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75f)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.ATTACK_KNOCKBACK, 0.1f);
    }

    @Override
    protected void registerGoals()
    {
        // Hard follow
        this.goalSelector.addGoal(0, new GoalDandoriFollowHard(this, 1.2, 6.0));
        // Attack target
        // Move to target
        // Move to patrol position
        // Wander around patrol position

        // Idle goals
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        // Target goals
    }

    @Override
    public void tick()
    {
        super.tick();

        if (getOwner() == null)
        {
            Player _nearestPlayer = level().getNearestPlayer(this, 128);
            if (_nearestPlayer != null) setOwner(_nearestPlayer);
        }
    }

    // --------------------------------------------------------------------------------------------
    // Animation stuff

    private static final String resourceName = "golem_cobble";

    protected String getResourcePath() {return "entity/legends/";}
    protected String getResourceName() {return resourceName;}

    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation." + resourceName + ".walk");
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation." + resourceName + ".idle");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemCobble pGolem = event.getAnimatable();
            event.getController().setAnimationSpeed(1.00);
            if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                return event.setAndContinue(ANIMATION_WALK);
            return event.setAndContinue(ANIMATION_IDLE);
        }));
    }

}
