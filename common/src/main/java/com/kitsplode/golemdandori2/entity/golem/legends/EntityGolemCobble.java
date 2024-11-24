package com.kitsplode.golemdandori2.entity.golem.legends;

import com.kitsplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
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

    // --------------------------------------------------------------------------------------------
    // Animation stuff

    protected String getResourcePath() {return "entity/legends/";}
    protected String getResourceName() {return "golem_cobble";}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "controller", 0, event ->
        {
//            EntityGolemCobble pGolem = event.getAnimatable();
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_cobble.idle"));
        }));
    }

}
