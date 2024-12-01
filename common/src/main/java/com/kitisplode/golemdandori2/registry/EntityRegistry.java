package com.kitisplode.golemdandori2.registry;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.entity.golem.legends.EntityGolemCobble;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class EntityRegistry {
	public static void init() {}

	public static final Supplier<EntityType<EntityGolemCobble>> ENTITY_GOLEM_COBBLE = registerEntity("golem_cobble", EntityGolemCobble::new, 0.8f, 0.8f, 0xFFFFFF, 0xFFFFFF);

	public static void registerEntityAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier> registrar)
	{
		registrar.accept(EntityRegistry.ENTITY_GOLEM_COBBLE.get(), EntityGolemCobble.createMobAttributes().build());
	}

	private static <T extends Mob> Supplier<EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> entity, float width, float height, int primaryEggColor, int secondaryEggColor) {
		return ExampleModCommon.COMMON_PLATFORM.registerEntity(name, () -> EntityType.Builder.of(entity, MobCategory.CREATURE).sized(width, height).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, name))));
	}
}
