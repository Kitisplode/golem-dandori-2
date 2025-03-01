package com.kitisplode.golemdandori2.entity.projectile;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

public class EntityProjectileOwnerAware extends Arrow implements GeoEntity
{
    private LivingEntity owner;
    private double aoeRange;
    private double damage;
    private boolean hasAoe;
    protected AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public EntityProjectileOwnerAware(EntityType<? extends Arrow> entityType, Level level)
    {
        super(entityType, level);
    }

    public void setHasAoE(boolean _hasAoe)
    {
        hasAoe = _hasAoe;
    }
    public void setAoeRange(double _aoeRange)
    {
        aoeRange = _aoeRange;
    }
    public void setAoeDamage(double _damage)
    {
        damage = _damage;
    }
    public void setArrowOwner(LivingEntity _owner)
    {
        owner = _owner;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (owner == null) this.discard();
    }

    @Override
    protected boolean canHitEntity(Entity _target)
    {
        if (_target != null)
        {
            // If the target is the owner, then do not hit.
            if (_target == owner) return false;
            // If our owner is something that could have an owner then...
            if (owner instanceof IEntityDandoriPik ownerPik)
            {
                LivingEntity ownerOwner = ownerPik.getOwner();
                // If the target is our owner's owner, then do not hit.
                if (_target == ownerOwner) return false;
                // If the target is also something that could have an owner too, then check for matching owners.
                if (_target instanceof IEntityDandoriPik targetPik)
                {
                    // If they have matching owners, do not hit.
                    if (targetPik.getOwner() == ownerOwner) return false;
                }
                if (_target instanceof TamableAnimal animal)
                {
                    if (animal.getOwner() == ownerOwner) return false;
                }
            }
            // Do not hit villagers (for now).
            if (_target instanceof Villager) return false;
        }
        // Otherwise, hit as normal.
        return super.canHitEntity(_target);
    }

    @Override
    protected void onHitEntity(EntityHitResult _hitResult)
    {
        super.onHitEntity(_hitResult);
        if (hasAoe) doAoe();
        setNoGravity(false);
    }

    @Override
    protected void onHitBlock(BlockHitResult _hitResult)
    {
        super.onHitBlock(_hitResult);
        if (hasAoe) doAoe();
        this.setNoGravity(false);
    }

    @Override
    protected ItemStack getPickupItem()
    {
        return ItemStack.EMPTY;
    }

    private void doAoe()
    {

    }

    // =================================================================================================================
    // Animation

    private static final String RESOURCE_NAME = "arrow";

    protected String getResourcePath() {return "entity/other/";}
    protected String getResourceName() {return RESOURCE_NAME;}

    public ResourceLocation getModel()
    {
        return ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "geo/" + getResourcePath() + getResourceName() + ".geo.json");
    }

    public ResourceLocation getTexture()
    {
        return ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "textures/" + getResourcePath() + getResourceName() + ".png");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
