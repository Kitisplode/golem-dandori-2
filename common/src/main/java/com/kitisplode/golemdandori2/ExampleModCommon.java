package com.kitisplode.golemdandori2;

import com.kitisplode.golemdandori2.platform.ExampleModPlatform;
import com.kitisplode.golemdandori2.registry.*;
import com.kitisplode.golemdandori2.util.DataDandoriCount;
import com.mojang.logging.LogUtils;
import commonnetwork.Constants;
import org.slf4j.Logger;

import java.util.ServiceLoader;

/**
 * Base multiloader class for the mod, handling all the common distribution tasks and holding common values
 */
public final class ExampleModCommon {
    public static final String MODID = "golemdandori2";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ExampleModPlatform COMMON_PLATFORM = ServiceLoader.load(ExampleModPlatform.class).findFirst().orElseThrow();

    public static void doRegistrations() {
        SoundRegistry.init();
        BlockRegistry.init();
        BlockEntityRegistry.init();
        EntityRegistry.init();
        ArmorMaterialRegistry.init();
        ItemRegistry.init();
        new PacketRegistry().init();
        DataDandoriCount.init();
    }
}
