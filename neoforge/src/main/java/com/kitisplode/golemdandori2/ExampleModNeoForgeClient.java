package com.kitisplode.golemdandori2;

import com.kitisplode.golemdandori2.client.ExampleModClient;
import com.kitisplode.golemdandori2.client.gui.HudDandoriCount;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

@EventBusSubscriber(modid = ExampleModCommon.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ExampleModNeoForgeClient {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        ExampleModClient.registerRenderers(event::registerEntityRenderer, event::registerBlockEntityRenderer);
    }

    @SubscribeEvent
    public static void registerGuiLayersEvent(RegisterGuiLayersEvent event)
    {
        event.registerAboveAll(fromNamespaceAndPath(ExampleModCommon.MODID, "hud_dandori"), HudDandoriCount.instance);
    }
}
