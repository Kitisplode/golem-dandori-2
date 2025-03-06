package com.kitisplode.golemdandori2.mixin;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DamageSource.class)
public class MixinDamageSource
{
    @Inject(method = "is",
            at = @At("HEAD"),
            cancellable = true)
    private void inject_is_allDamageIsBypassCooldown(TagKey<DamageType> damageTypeKey, CallbackInfoReturnable<Boolean> cir)
    {
        if (damageTypeKey == DamageTypeTags.BYPASSES_COOLDOWN)
        {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
