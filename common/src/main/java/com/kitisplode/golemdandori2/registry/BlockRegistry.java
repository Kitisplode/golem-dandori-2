package com.kitisplode.golemdandori2.registry;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.geckolib.block.FertilizerBlock;
import com.kitisplode.golemdandori2.geckolib.block.GeckoHabitatBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public final class BlockRegistry {
	public static void init() {}

	public static final Supplier<GeckoHabitatBlock> GECKO_HABITAT = registerBlock("gecko_habitat", properties -> new GeckoHabitatBlock(properties.noOcclusion()));
	public static final Supplier<FertilizerBlock> FERTILIZER = registerBlock("fertilizer", properties -> new FertilizerBlock(properties.noOcclusion()));

	private static <T extends Block> Supplier<T> registerBlock(String id, Function<BlockBehaviour.Properties, T> block) {
		return ExampleModCommon.COMMON_PLATFORM.registerBlock(id, block);
	}
}
