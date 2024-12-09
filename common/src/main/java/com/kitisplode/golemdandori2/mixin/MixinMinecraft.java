package com.kitisplode.golemdandori2.mixin;

import com.kitisplode.golemdandori2.item.IItemSwingUse;
import com.kitisplode.golemdandori2.network.C2SPacketItemSwingUse;
import com.kitisplode.golemdandori2.network.C2SPacketItemSwingUseTick;
import commonnetwork.api.Dispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class)
public abstract class MixinMinecraft
{
    @Shadow @Nullable
    public LocalPlayer player;

    @Inject(method = "startAttack()Z",
            at = @At("HEAD"),//@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.BY, by = 3),
            cancellable = true)
    private void inject_doAttack_swingDandoriItem(CallbackInfoReturnable<Boolean> cir)
    {
        if (this.player == null) return;

        ItemStack itemStack = this.player.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.getItem() instanceof IItemSwingUse itemDandori)
        {
            Dispatcher.sendToServer(new C2SPacketItemSwingUse());
            itemDandori.swing(this.player);
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "continueAttack(Z)V",
            at = @At("HEAD"),
            cancellable = true)
    private void inject_handleBlockBreaking_swingDandoriItem(boolean pLeftClick, CallbackInfo ci)
    {
        if (!pLeftClick) return;
        if (this.player == null) return;
        ItemStack itemStack = this.player.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.getItem() instanceof IItemSwingUse itemDandori)
        {
            Dispatcher.sendToServer(new C2SPacketItemSwingUseTick());
            itemDandori.swingTick(this.player);
            ci.cancel();
        }
    }
}
