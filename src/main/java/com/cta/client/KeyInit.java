package com.cta.client;

import com.cta.CTA;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = CTA.MODID, value = Dist.CLIENT)
public class KeyInit {
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.registerCategory(CameraHandler.CATEGORY);
        event.register(CameraHandler.KEY_SCOPE_UP);
        event.register(CameraHandler.KEY_SCOPE_DOWN);
        event.register(CameraHandler.KEY_EXIT_SCOPE);
    }
}
