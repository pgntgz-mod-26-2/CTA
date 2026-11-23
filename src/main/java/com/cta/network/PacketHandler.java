package com.cta.network;

import com.cta.CTA;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CTA.MODID)
public class PacketHandler {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SetCameraViewPacket.TYPE, SetCameraViewPacket.STREAM_CODEC, SetCameraViewPacket::handle);
        registrar.playToClient(StartScopeViewPacket.TYPE, StartScopeViewPacket.STREAM_CODEC, StartScopeViewPacket::handle);
        registrar.playToClient(OpenChannelScreenPacket.TYPE, OpenChannelScreenPacket.STREAM_CODEC, OpenChannelScreenPacket::handle);
        registrar.playToClient(ScopeRangeDataPacket.TYPE, ScopeRangeDataPacket.STREAM_CODEC, ScopeRangeDataPacket::handle);
        registrar.playToServer(StopViewingPacket.TYPE, StopViewingPacket.STREAM_CODEC, StopViewingPacket::handle);
        registrar.playToServer(SetChannelPacket.TYPE, SetChannelPacket.STREAM_CODEC, SetChannelPacket::handle);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
