package com.cta.network;

import com.cta.CTA;
import com.cta.client.ClientPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OpenChannelScreenPacket implements CustomPacketPayload {
    public static final Type<OpenChannelScreenPacket> TYPE = new Type<>(CTA.id("open_channel_screen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenChannelScreenPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, p -> p.peripheralPos,
            BlockPos.STREAM_CODEC, p -> p.targetPos,
            OpenChannelScreenPacket::new
    );

    public final BlockPos peripheralPos;
    public final BlockPos targetPos;

    public OpenChannelScreenPacket(BlockPos peripheralPos, BlockPos targetPos) {
        this.peripheralPos = peripheralPos;
        this.targetPos = targetPos;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenChannelScreenPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleOpenChannelScreenPacket(msg));
    }
}
