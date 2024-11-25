package com.kitsplode.golemdandori2;

import com.kitsplode.golemdandori2.platform.ExampleModPlatform;
import com.kitsplode.golemdandori2.registry.*;

import java.util.ServiceLoader;

/**
 * Base multiloader class for the mod, handling all the common distribution tasks and holding common values
 */
public final class ExampleModCommon {
    public static final String MODID = "golemdandori2";

    public static final ExampleModPlatform COMMON_PLATFORM = ServiceLoader.load(ExampleModPlatform.class).findFirst().orElseThrow();

    public static void doRegistrations() {
        SoundRegistry.init();
        BlockRegistry.init();
        BlockEntityRegistry.init();
        EntityRegistry.init();
        ArmorMaterialRegistry.init();
        ItemRegistry.init();
    }
}
