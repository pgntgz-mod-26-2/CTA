package com.cta.client.renderer;

import com.cta.entity.MissileEntity;
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
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MissileRenderer extends EntityRenderer<MissileEntity, MissileRenderer.MissileRenderState> {
    private final ItemModelResolver itemModelResolver;

    public MissileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public MissileRenderState createRenderState() {
        return new MissileRenderState();
    }

    @Override
    public void extractRenderState(MissileEntity entity, MissileRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        ItemStack modelItem = entity.getModelItem();
        if (modelItem.isEmpty()) {
            state.item.clear();
            return;
        }

        state.yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        state.pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        state.scale = entity.isDeployed() ? getVelocityBasedScale(entity.getDeltaMovement()) : 1.0f;
        this.itemModelResolver.updateForNonLiving(state.item, modelItem, ItemDisplayContext.NONE, entity);
    }

    @Override
    protected AABB getBoundingBoxForCulling(MissileEntity entity) {
        return entity.getBoundingBox().inflate(1.0);
    }

    @Override
    public void submit(MissileRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (state.item.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        float roll = 0.0f;

        poseStack.translate(0, 0.25, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - state.yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-state.pitch));
        poseStack.mulPose(Axis.ZP.rotationDegrees(roll));
        poseStack.translate(0, 0.25, 0);

        poseStack.scale(state.scale, state.scale, state.scale);

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

    public float getVelocityBasedScale(Vec3 velocity) {
        double speed = velocity.length();
        return (float) Math.min(1.0 + speed * 0.1, 1.5);
    }

    public static class MissileRenderState extends EntityRenderState {
        public final ItemStackRenderState item = new ItemStackRenderState();
        public float yaw;
        public float pitch;
        public float scale = 1.0f;
    }
}
