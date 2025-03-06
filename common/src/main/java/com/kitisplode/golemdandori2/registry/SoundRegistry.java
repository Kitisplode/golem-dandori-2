package com.kitisplode.golemdandori2.registry;

import com.kitisplode.golemdandori2.ExampleModCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public final class SoundRegistry {
	public static void init() {}

	public static Supplier<SoundEvent> ENTITY_GOLEM_COBBLE_YES = registerSound("entity_golem_cobble_yes",
			() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "entity_golem_cobble_yes")));
	public static Supplier<SoundEvent> ENTITY_GOLEM_COBBLE_IDLE = registerSound("entity_golem_cobble_idle",
			() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "entity_golem_cobble_idle")));
	public static Supplier<SoundEvent> ENTITY_GOLEM_COBBLE_ARMS = registerSound("entity_golem_cobble_arms",
			() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "entity_golem_cobble_arms")));
	public static Supplier<SoundEvent> ENTITY_GOLEM_COBBLE_ORDERED = registerSound("entity_golem_cobble_ordered",
			() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "entity_golem_cobble_ordered")));

	public static Supplier<SoundEvent> ENTITY_GOLEM_PLANK_YES = registerSound("entity_golem_plank_yes",
			() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "entity_golem_plank_yes")));
	public static Supplier<SoundEvent> ENTITY_GOLEM_PLANK_SHOOT = registerSound("entity_golem_plank_shoot",
			() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "entity_golem_plank_shoot")));
	public static Supplier<SoundEvent> ENTITY_GOLEM_PLANK_ORDERED = registerSound("entity_golem_plank_ordered",
			() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "entity_golem_plank_ordered")));

	public static Supplier<SoundEvent> ENTITY_GOLEM_GRINDSTONE_RIDE = registerSound("entity_golem_grindstone_ride",
			() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "entity_golem_grindstone_ride")));

	public static Supplier<SoundEvent> JACK_MUSIC = registerSound("jack_in_the_box_music", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "jack_in_the_box_music")));

	private static <T extends SoundEvent> Supplier<T> registerSound(String id, Supplier<T> sound) {
		return ExampleModCommon.COMMON_PLATFORM.registerSound(id, sound);
	}
}
