package com.cta.network;

import com.cta.CTA;
import com.cta.client.ClientPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class StartScopeViewPacket implements CustomPacketPayload {
    public static final Type<StartScopeViewPacket> TYPE = new Type<>(CTA.id("start_scope_view"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StartScopeViewPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, p -> p.scopePos,
            StartScopeViewPacket::new
    );

    public final BlockPos scopePos;

    public StartScopeViewPacket(BlockPos scopePos) {
        this.scopePos = scopePos;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StartScopeViewPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleStartScopeViewPacket(msg));
    }
}
