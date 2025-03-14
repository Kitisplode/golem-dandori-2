package com.kitisplode.golemdandori2.client.model.entity;

import com.kitisplode.golemdandori2.entity.golem.AbstractGolemDandoriPik;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.ArrayList;

public class ModelGolem extends GeoModel<AbstractGolemDandoriPik>
{

    @Override
    public ResourceLocation getModelResource(AbstractGolemDandoriPik animatable, @Nullable GeoRenderer<AbstractGolemDandoriPik> renderer)
    {
        return animatable.getModel();
    }

    @Override
    public ResourceLocation getTextureResource(AbstractGolemDandoriPik animatable, @Nullable GeoRenderer<AbstractGolemDandoriPik> renderer)
    {
        return animatable.getTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(AbstractGolemDandoriPik animatable)
    {
        return animatable.getAnimations();
    }

    @Override
    public void setCustomAnimations(AbstractGolemDandoriPik animatable, long instanceId, AnimationState<AbstractGolemDandoriPik> animationState)
    {

    }

    //==================================================================================================================
    protected ArrayList<SavedBone> savedBones = new ArrayList<>();

    public void resetCustomAnimations()
    {
        for (SavedBone bone : savedBones)
        {
            bone.setBone();
        }
        savedBones.clear();
    }

    protected class SavedBone
    {
        public enum TYPES {ROTX,ROTY,ROTZ, TLX,TLY,TLZ, SCX,SCY,SCZ};
        private final float amount;
        private final String boneName;
        private final TYPES type;
        public SavedBone(float amount, String bone, TYPES type)
        {
            this.amount = amount;
            this.boneName = bone;
            this.type = type;
        }
        public void setBone()
        {
            GeoBone bone = getAnimationProcessor().getBone(this.boneName);
            if (bone != null)
            {
                switch (type)
                {
                    case ROTX -> bone.setRotX(amount);
                    case ROTY -> bone.setRotY(amount);
                    case ROTZ -> bone.setRotZ(amount);
                    case TLX -> bone.setPosX(amount);
                    case TLY -> bone.setPosY(amount);
                    case TLZ -> bone.setPosZ(amount);
                    case SCX -> bone.setScaleX(amount);
                    case SCY -> bone.setScaleY(amount);
                    case SCZ -> bone.setScaleZ(amount);
                }
            }
        }
    }
}
