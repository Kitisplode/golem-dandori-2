package com.kitisplode.golemdandori2.registry;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.entity.golem.legends.EntityGolemCobble;
import com.kitisplode.golemdandori2.entity.golem.legends.EntityGolemPlank;
import com.kitisplode.golemdandori2.entity.projectile.EntityProjectileOwnerAware;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class EntityRegistry {
	public static void init() {}

	public static final Supplier<EntityType<EntityProjectileOwnerAware>> ENTITY_PROJECTILE_GOLEM = ExampleModCommon.COMMON_PLATFORM.registerEntity("projectile_golem",
			() -> EntityType.Builder.of(EntityProjectileOwnerAware::new, MobCategory.MISC)
					.sized(0.75f,0.75f)
					.clientTrackingRange(4)
					.updateInterval(20)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "projectile_golem"))));
//			registerEntity("projectile_golem", EntityProjectileOwnerAware::new, 0.75f, 0.7f);

	public static final Supplier<EntityType<EntityGolemCobble>> ENTITY_GOLEM_COBBLE = registerMob("golem_cobble", EntityGolemCobble::new, 0.8f,0.8f);
	public static final Supplier<EntityType<EntityGolemPlank>> ENTITY_GOLEM_PLANK = registerMob("golem_plank", EntityGolemPlank::new, 0.8f,0.8f);

	public static void registerEntityAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier> registrar)
	{
		registrar.accept(EntityRegistry.ENTITY_GOLEM_COBBLE.get(), EntityGolemCobble.createMobAttributes().build());
		registrar.accept(EntityRegistry.ENTITY_GOLEM_PLANK.get(), EntityGolemPlank.createMobAttributes().build());
	}



	private static <T extends Mob> Supplier<EntityType<T>> registerMob(String name, EntityType.EntityFactory<T> entity, float width, float height) {
		return ExampleModCommon.COMMON_PLATFORM.registerEntity(name, () -> EntityType.Builder.of(entity, MobCategory.CREATURE).sized(width, height).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, name))));
	}

	private static <T extends Entity> Supplier<EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> entity, float width, float height)
	{
		return ExampleModCommon.COMMON_PLATFORM.registerEntity(name, () -> EntityType.Builder.of(entity, MobCategory.MISC).sized(width, height).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, name))));
	}
}
