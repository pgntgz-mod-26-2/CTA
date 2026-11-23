package com.cta;

import com.cta.compat.cc.CCCompat;
import com.cta.config.MissileConfig;
import com.cta.entity.MissileEntity;
import com.cta.registry.ModBlockEntities;
import com.cta.registry.ModBlocks;
import com.cta.registry.ModCreativeTabs;
import com.cta.registry.ModEntities;
import com.cta.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(CTA.MODID)
public class CTA {
    public static final String MODID = "cta";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CTA(IEventBus modEventBus, ModContainer modContainer) {
        MissileConfig.register(modContainer);

        ModCreativeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(MissileEntity::registerTicketController);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CCCompat::init);
    }
}
