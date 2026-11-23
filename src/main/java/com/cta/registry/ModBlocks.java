package com.cta.registry;

import com.cta.CTA;
import com.cta.block.ScopeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CTA.MODID);

    private static BlockBehaviour.Properties scopeProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.0f, 6.0f)
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    public static final DeferredBlock<ScopeBlock> SCOPE_BLOCK = BLOCKS.registerBlock(
            "scope", ScopeBlock::new, ModBlocks::scopeProperties);

    public static final DeferredBlock<ScopeBlock> THERMAL_SCOPE_BLOCK = BLOCKS.registerBlock(
            "thermal_scope", ScopeBlock::new, ModBlocks::scopeProperties);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
