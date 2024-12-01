package com.kitisplode.golemdandori2.network;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.item.IItemSwingUse;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class C2SPacketItemSwingUseTick
{
    public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "c2s_item_swing_use_tick");
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SPacketItemSwingUseTick> STREAM_CODEC = StreamCodec.ofMember(C2SPacketItemSwingUseTick::encode,
            C2SPacketItemSwingUseTick::new);

    public C2SPacketItemSwingUseTick() {}

    public C2SPacketItemSwingUseTick(FriendlyByteBuf buf) {}

    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }

    public void encode(FriendlyByteBuf buf) {}

    public static void handle(PacketContext<C2SPacketItemSwingUseTick> context)
    {
        if (Side.CLIENT.equals(context.side()))
        {
        }
        else
        {
            ServerPlayer _player = context.sender();
            if (_player != null)
            {
                ItemStack itemStack = _player.getItemInHand(InteractionHand.MAIN_HAND);
                if (itemStack.getItem() instanceof IItemSwingUse _item)
                {
                    if (!_player.getCooldowns().isOnCooldown(itemStack))
                        _item.swingTick(_player);
                }
            }
        }
    }
}
