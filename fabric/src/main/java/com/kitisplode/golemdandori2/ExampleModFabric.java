package com.kitisplode.golemdandori2;

import com.kitisplode.golemdandori2.registry.EntityRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public final class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ExampleModCommon.doRegistrations();
        EntityRegistry.registerEntityAttributes(FabricDefaultAttributeRegistry::register);
    }
}
