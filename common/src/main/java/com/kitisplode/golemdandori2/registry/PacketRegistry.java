package com.kitisplode.golemdandori2.registry;

import com.kitisplode.golemdandori2.network.C2SPacketItemSwingUse;
import com.kitisplode.golemdandori2.network.C2SPacketItemSwingUseTick;
import commonnetwork.api.Network;

public class PacketRegistry
{
    public void init()
    {
        Network
                .registerPacket(C2SPacketItemSwingUse.type(), C2SPacketItemSwingUse.class, C2SPacketItemSwingUse.STREAM_CODEC, C2SPacketItemSwingUse::handle)
                .registerPacket(C2SPacketItemSwingUseTick.type(), C2SPacketItemSwingUseTick.class, C2SPacketItemSwingUseTick.STREAM_CODEC, C2SPacketItemSwingUseTick::handle);
    }

}
