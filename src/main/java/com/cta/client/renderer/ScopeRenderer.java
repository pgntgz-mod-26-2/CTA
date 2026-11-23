package com.cta.client.renderer;

import com.cta.entity.ScopeEntity;
import com.cta.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScopeRenderer extends EntityRenderer<ScopeEntity, ScopeRenderer.ScopeRenderState> {
    private final ItemModelResolver itemModelResolver;

    public ScopeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public ScopeRenderState createRenderState() {
        return new ScopeRenderState();
    }

    @Override
    public void extractRenderState(ScopeEntity entity, ScopeRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.storedYaw = entity.getStoredYaw();
        state.storedPitch = entity.getStoredPitch();

        ItemStack modelItem = entity.getModelItem();
        if (modelItem.isEmpty()) {
            modelItem = new ItemStack(ModItems.PANTHER_SCOPE.get());
        }
        this.itemModelResolver.updateForNonLiving(state.item, modelItem, ItemDisplayContext.FIXED, entity);
    }

    @Override
    public void submit(ScopeRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();

        float yaw = state.storedYaw;
        float pitch = state.storedPitch;
        float roll = 0.0f;

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
        poseStack.mulPose(Axis.ZP.rotationDegrees(roll));

        poseStack.scale(1.0f, 1.0f, 1.0f);

        state.item.submit(
            poseStack,
            submitNodeCollector,
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            state.outlineColor
        );

        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, cameraRenderState);
    }

    public static class ScopeRenderState extends EntityRenderState {
        public final ItemStackRenderState item = new ItemStackRenderState();
        public float storedYaw;
        public float storedPitch;
    }
}
