package com.cta.client;

import com.cta.CTA;
import com.cta.registry.ModEntities;
import com.cta.client.renderer.MissileRenderer;
import com.cta.client.renderer.ScopeRenderer;
import com.cta.client.renderer.FragmentRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CTA.MODID, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MISSILE.get(), MissileRenderer::new);
        event.registerEntityRenderer(ModEntities.SCOPE.get(), ScopeRenderer::new);
        event.registerEntityRenderer(ModEntities.THERMAL_SCOPE.get(), ScopeRenderer::new);
        event.registerEntityRenderer(ModEntities.FRAGMENT.get(), FragmentRenderer::new);
    }
}
