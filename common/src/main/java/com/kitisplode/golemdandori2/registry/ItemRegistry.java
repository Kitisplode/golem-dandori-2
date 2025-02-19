package com.kitisplode.golemdandori2.registry;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.geckolib.item.GeckoArmorItem;
import com.kitisplode.golemdandori2.geckolib.item.WolfArmorItem;
import com.kitisplode.golemdandori2.item.ItemDandoriCall;
import com.kitisplode.golemdandori2.item.ItemDandoriDig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ItemRegistry {
	public static void init() {}

//	public static final Supplier<BlockItem> GECKO_HABITAT = registerItem("gecko_habitat", properties -> new GeckoHabitatItem(BlockRegistry.GECKO_HABITAT.get(), properties));
//	public static final Supplier<BlockItem> FERTILIZER = registerItem("fertilizer", properties -> new BlockItem(BlockRegistry.FERTILIZER.get(), properties));
//
//	public static final Supplier<JackInTheBoxItem> JACK_IN_THE_BOX = registerItem("jack_in_the_box", JackInTheBoxItem::new);
//
	public static final Supplier<WolfArmorItem> WOLF_ARMOR_HELMET = registerItem("wolf_armor_helmet", properties -> new WolfArmorItem(ArmorMaterialRegistry.WOLF_ARMOR_MATERIAL, ArmorType.HELMET, properties));
	public static final Supplier<WolfArmorItem> WOLF_ARMOR_CHESTPLATE = registerItem("wolf_armor_chestplate", properties -> new WolfArmorItem(ArmorMaterialRegistry.WOLF_ARMOR_MATERIAL, ArmorType.CHESTPLATE, properties));
	public static final Supplier<WolfArmorItem> WOLF_ARMOR_LEGGINGS = registerItem("wolf_armor_leggings", properties -> new WolfArmorItem(ArmorMaterialRegistry.WOLF_ARMOR_MATERIAL, ArmorType.LEGGINGS, properties));
	public static final Supplier<WolfArmorItem> WOLF_ARMOR_BOOTS = registerItem("wolf_armor_boots", properties -> new WolfArmorItem(ArmorMaterialRegistry.WOLF_ARMOR_MATERIAL, ArmorType.BOOTS, properties));

	public static final Supplier<GeckoArmorItem> GECKO_ARMOR_HELMET = registerItem("gecko_armor_helmet", properties -> new GeckoArmorItem(ArmorMaterialRegistry.GECKO_ARMOR_MATERIAL, ArmorType.HELMET, properties));
	public static final Supplier<GeckoArmorItem> GECKO_ARMOR_CHESTPLATE = registerItem("gecko_armor_chestplate", properties -> new GeckoArmorItem(ArmorMaterialRegistry.GECKO_ARMOR_MATERIAL, ArmorType.CHESTPLATE, properties));
	public static final Supplier<GeckoArmorItem> GECKO_ARMOR_LEGGINGS = registerItem("gecko_armor_leggings", properties -> new GeckoArmorItem(ArmorMaterialRegistry.GECKO_ARMOR_MATERIAL, ArmorType.LEGGINGS, properties));
	public static final Supplier<GeckoArmorItem> GECKO_ARMOR_BOOTS = registerItem("gecko_armor_boots", properties -> new GeckoArmorItem(ArmorMaterialRegistry.GECKO_ARMOR_MATERIAL, ArmorType.BOOTS, properties));
//
//    public static final Supplier<SpawnEggItem> BAT_SPAWN_EGG = registerItem("bat_spawn_egg", properties -> ExampleModCommon.COMMON_PLATFORM.makeSpawnEggFor(EntityRegistry.BAT, 0x1F1F1F, 0x0D0D0D, properties));
//    public static final Supplier<SpawnEggItem> BIKE_SPAWN_EGG = registerItem("bike_spawn_egg", properties -> ExampleModCommon.COMMON_PLATFORM.makeSpawnEggFor(EntityRegistry.BIKE, 0xD3E3E6, 0xE9F1F5, properties));
//    public static final Supplier<SpawnEggItem> RACE_CAR_SPAWN_EGG = registerItem("race_car_spawn_egg", properties -> ExampleModCommon.COMMON_PLATFORM.makeSpawnEggFor(EntityRegistry.RACE_CAR, 0x9E1616, 0x595959, properties));
//    public static final Supplier<SpawnEggItem> PARASITE_SPAWN_EGG = registerItem("parasite_spawn_egg", properties -> ExampleModCommon.COMMON_PLATFORM.makeSpawnEggFor(EntityRegistry.PARASITE, 0x302219, 0xACACAC, properties));
//    public static final Supplier<SpawnEggItem> MUTANT_ZOMBIE_SPAWN_EGG = registerItem("mutant_zombie_spawn_egg", properties -> ExampleModCommon.COMMON_PLATFORM.makeSpawnEggFor(EntityRegistry.MUTANT_ZOMBIE, 0x3C6236, 0x579989, properties));
//    public static final Supplier<SpawnEggItem> FAKE_GLASS_SPAWN_EGG = registerItem("fake_glass_spawn_egg", properties -> ExampleModCommon.COMMON_PLATFORM.makeSpawnEggFor(EntityRegistry.FAKE_GLASS, 0xDD0000, 0xD8FFF7, properties));
//    public static final Supplier<SpawnEggItem> COOL_KID_SPAWN_EGG = registerItem("cool_kid_spawn_egg", properties -> ExampleModCommon.COMMON_PLATFORM.makeSpawnEggFor(EntityRegistry.COOL_KID, 0x5F2A31, 0x6F363E, properties));
//    public static final Supplier<SpawnEggItem> GREMLIN_SPAWN_EGG = registerItem("gremlin_spawn_egg", properties -> ExampleModCommon.COMMON_PLATFORM.makeSpawnEggFor(EntityRegistry.GREMLIN, 0x505050, 0x606060, properties));

	public static final Supplier<SpawnEggItem> SPAWN_EGG_GOLEM_COBBLE = registerItem("golem_cobble_spawn_egg", properties -> ExampleModCommon.COMMON_PLATFORM.makeSpawnEggFor(EntityRegistry.ENTITY_GOLEM_COBBLE, 0xFFFFFF, 0xFFFFFF, properties));
	public static final Supplier<ItemDandoriCall> ITEM_DANDORI_CALL = registerItem("item_dandori_banner", properties -> new ItemDandoriCall(properties.stacksTo(1)));
	public static final Supplier<ItemDandoriDig> ITEM_DANDORI_DIG = registerItem("item_dandori_dig", properties -> new ItemDandoriDig(properties.stacksTo(1)));


	private static <T extends Item> Supplier<T> registerItem(String id, Function<Item.Properties, T> item) {
		return ExampleModCommon.COMMON_PLATFORM.registerItem(id, item);
	}

	public static final Supplier<CreativeModeTab> GOLEMDANDORI_TAB = ExampleModCommon.COMMON_PLATFORM.registerCreativeModeTab("golemdandori_items", () -> ExampleModCommon.COMMON_PLATFORM.newCreativeTabBuilder()
			.title(Component.translatable("itemGroup." + ExampleModCommon.MODID + ".golemdandori_items"))
			.icon(() -> new ItemStack(ItemRegistry.ITEM_DANDORI_CALL.get()))
			.displayItems((enabledFeatures, entries) -> {
				entries.accept(ItemRegistry.ITEM_DANDORI_CALL.get());
				entries.accept(ItemRegistry.ITEM_DANDORI_DIG.get());
				entries.accept(ItemRegistry.SPAWN_EGG_GOLEM_COBBLE.get());
			})
			.build()
	);
}
