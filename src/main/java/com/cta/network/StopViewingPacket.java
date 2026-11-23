package com.cta.network;

import com.cta.CTA;
import com.cta.block.ScopeBlockEntity;
import com.cta.entity.CameraEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;

public class StopViewingPacket implements CustomPacketPayload {
    public static final Type<StopViewingPacket> TYPE = new Type<>(CTA.id("stop_viewing"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StopViewingPacket> STREAM_CODEC =
            StreamCodec.of(StopViewingPacket::write, StopViewingPacket::read);

    @Nullable
    private final BlockPos scopePos;

    public StopViewingPacket() {
        this.scopePos = null;
    }

    public StopViewingPacket(@Nullable BlockPos scopePos) {
        this.scopePos = scopePos;
    }

    private static void write(RegistryFriendlyByteBuf buf, StopViewingPacket msg) {
        boolean hasPos = msg.scopePos != null;
        buf.writeBoolean(hasPos);
        if (hasPos) {
            BlockPos.STREAM_CODEC.encode(buf, msg.scopePos);
        }
    }

    private static StopViewingPacket read(RegistryFriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            return new StopViewingPacket(BlockPos.STREAM_CODEC.decode(buf));
        }
        return new StopViewingPacket();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StopViewingPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            if (msg.scopePos != null) {
                BlockEntity be = player.level().getBlockEntity(msg.scopePos);
                if (be instanceof ScopeBlockEntity scopeBE) {
                    scopeBE.stopViewing(player);
                }
            } else {
                BlockPos center = player.blockPosition();
                for (int dx = -8; dx <= 8; dx++) {
                    for (int dy = -8; dy <= 8; dy++) {
                        for (int dz = -8; dz <= 8; dz++) {
                            BlockPos checkPos = center.offset(dx, dy, dz);
                            BlockEntity be = player.level().getBlockEntity(checkPos);
                            if (be instanceof ScopeBlockEntity scopeBE) {
                                scopeBE.stopViewing(player);
                            }
                        }
                    }
                }
            }

            Entity camera = player.getCamera();
            if (camera instanceof CameraEntity cam) {
                cam.stopViewing(player);
            }
        });
    }
}
