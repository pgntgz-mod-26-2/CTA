package com.cta.network;

import com.cta.CTA;
import com.cta.item.WirelessConnectorItem;
import com.cta.wireless.WirelessLinkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SetChannelPacket implements CustomPacketPayload {
    public static final Type<SetChannelPacket> TYPE = new Type<>(CTA.id("set_channel"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetChannelPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, p -> p.peripheralPos,
            BlockPos.STREAM_CODEC, p -> p.targetPos,
            ByteBufCodecs.stringUtf8(32), p -> p.channel,
            SetChannelPacket::new
    );

    private final BlockPos peripheralPos;
    private final BlockPos targetPos;
    private final String channel;

    public SetChannelPacket(BlockPos peripheralPos, BlockPos targetPos, String channel) {
        this.peripheralPos = peripheralPos;
        this.targetPos = targetPos;
        this.channel = channel;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetChannelPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            if (player.distanceToSqr(msg.targetPos.getX() + 0.5, msg.targetPos.getY() + 0.5, msg.targetPos.getZ() + 0.5) > 64) return;

            String sanitized = msg.channel.replaceAll("[^a-zA-Z0-9_\\-]", "");
            if (sanitized.isEmpty() || sanitized.length() > 32) return;

            ServerLevel level = player.level();
            WirelessLinkSavedData data = WirelessLinkSavedData.get(level);
            data.addLink(msg.targetPos, sanitized, msg.peripheralPos);

            clearConnectorPeripheral(player.getMainHandItem());
            clearConnectorPeripheral(player.getOffhandItem());

            player.sendOverlayMessage(Component.literal("Channel '" + sanitized + "' connected!"));

            level.updateNeighborsAt(msg.targetPos, level.getBlockState(msg.targetPos).getBlock(), null);
        });
    }

    private static void clearConnectorPeripheral(ItemStack held) {
        if (held.getItem() instanceof WirelessConnectorItem) {
            CustomData.update(DataComponents.CUSTOM_DATA, held, tag -> {
                tag.remove("PeripheralPos");
                tag.remove("PeripheralDim");
            });
        }
    }
}
