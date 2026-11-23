package com.cta.registry;

import com.cta.CTA;
import com.cta.entity.FragmentEntity;
import com.cta.entity.MissileEntity;
import com.cta.entity.ScopeEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, CTA.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<MissileEntity>> MISSILE = ENTITIES.register("missile",
            () -> EntityType.Builder.<MissileEntity>of(MissileEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(32)
                    .updateInterval(1)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, CTA.id("missile"))));

    public static final DeferredHolder<EntityType<?>, EntityType<ScopeEntity>> SCOPE = ENTITIES.register("scope",
            () -> EntityType.Builder.<ScopeEntity>of(ScopeEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, CTA.id("scope"))));

    public static final DeferredHolder<EntityType<?>, EntityType<ScopeEntity>> THERMAL_SCOPE = ENTITIES.register("thermal_scope",
            () -> EntityType.Builder.<ScopeEntity>of(ScopeEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, CTA.id("thermal_scope"))));

    public static final DeferredHolder<EntityType<?>, EntityType<FragmentEntity>> FRAGMENT = ENTITIES.register("fragment",
            () -> EntityType.Builder.<FragmentEntity>of(FragmentEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.1f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, CTA.id("fragment"))));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
