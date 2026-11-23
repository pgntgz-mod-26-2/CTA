package com.cta.network;

import com.cta.CTA;
import com.cta.client.ClientPacketHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SetCameraViewPacket implements CustomPacketPayload {
    public static final Type<SetCameraViewPacket> TYPE = new Type<>(CTA.id("set_camera_view"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetCameraViewPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p -> p.entityId,
            SetCameraViewPacket::new
    );

    public final int entityId;

    public SetCameraViewPacket(Entity entity) {
        this.entityId = entity != null ? entity.getId() : -1;
    }

    public SetCameraViewPacket(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetCameraViewPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleSetCameraViewPacket(msg));
    }
}
