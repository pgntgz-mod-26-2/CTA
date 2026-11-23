package com.cta.registry;

import com.cta.CTA;
import com.cta.block.ScopeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CTA.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ScopeBlockEntity>> SCOPE_BE =
            BLOCK_ENTITIES.register("scope", () -> new BlockEntityType<>(
                    ScopeBlockEntity::new,
                    Set.of(ModBlocks.SCOPE_BLOCK.get(), ModBlocks.THERMAL_SCOPE_BLOCK.get())
            ));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
