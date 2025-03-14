package com.kitisplode.golemdandori2.client.model.entity;

import com.kitisplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import com.kitisplode.golemdandori2.entity.golem.legends.EntityGolemGrindstone;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class ModelGolemGrindstone extends ModelGolem
{
    @Override
    public void setCustomAnimations(AbstractGolemDandoriPik animatable, long instanceId, AnimationState<AbstractGolemDandoriPik> animationState)
    {
        if (animatable instanceof EntityGolemGrindstone golemGrindstone && !animatable.getPassengers().isEmpty())
        {
            GeoBone whole = getAnimationProcessor().getBone("whole");
            if (whole != null)
            {
                float speed = golemGrindstone.getAccel();
                float turnSpeed = golemGrindstone.getTurnSpeed();
                this.savedBones.add(new SavedBone(whole.getRotZ(), "whole", SavedBone.TYPES.ROTZ));
                this.savedBones.add(new SavedBone(whole.getRotX(), "whole", SavedBone.TYPES.ROTX));
                whole.setRotZ(-turnSpeed * 0.25f);
                whole.setRotX(-speed * 0.05f);
            }

            GeoBone head = getAnimationProcessor().getBone("head");
            if (head != null)
            {
                float wheel = golemGrindstone.getWheelRotation();
                this.savedBones.add(new SavedBone(head.getRotX(), "head", SavedBone.TYPES.ROTX));
                head.setRotX(-wheel * Mth.RAD_TO_DEG * 0.1f);
            }
        }
    }
}
