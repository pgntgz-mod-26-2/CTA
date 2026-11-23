package com.cta.mixin;

import com.cta.entity.CameraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "shouldRender(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void cta$hidePlayerWhenInScope(Entity entity, Frustum frustum, double camX, double camY, double camZ,
            CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();

        Entity cameraEntity = mc.getCameraEntity();
        if (cameraEntity instanceof CameraEntity) {
            if (entity == mc.player) {
                cir.setReturnValue(false);
            }
        }
    }
}
