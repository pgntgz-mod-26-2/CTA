package com.cta.network;

import com.cta.CTA;
import com.cta.client.ClientPacketHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ScopeRangeDataPacket implements CustomPacketPayload {
    public static final Type<ScopeRangeDataPacket> TYPE = new Type<>(CTA.id("scope_range_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ScopeRangeDataPacket> STREAM_CODEC =
            StreamCodec.of(ScopeRangeDataPacket::write, ScopeRangeDataPacket::read);

    public final boolean hasCannon;
    public final String shellName;
    public final double currentRange;
    public final int loadedCharges;
    public final int[] levels;
    public final double[] ranges;

    public ScopeRangeDataPacket(boolean hasCannon, String shellName, double currentRange,
                                int loadedCharges, int[] levels, double[] ranges) {
        this.hasCannon = hasCannon;
        this.shellName = shellName;
        this.currentRange = currentRange;
        this.loadedCharges = loadedCharges;
        this.levels = levels;
        this.ranges = ranges;
    }

    private static void write(RegistryFriendlyByteBuf buf, ScopeRangeDataPacket msg) {
        buf.writeBoolean(msg.hasCannon);
        buf.writeUtf(msg.shellName);
        buf.writeDouble(msg.currentRange);
        buf.writeInt(msg.loadedCharges);
        buf.writeInt(msg.levels.length);
        for (int i = 0; i < msg.levels.length; i++) {
            buf.writeInt(msg.levels[i]);
            buf.writeDouble(msg.ranges[i]);
        }
    }

    private static ScopeRangeDataPacket read(RegistryFriendlyByteBuf buf) {
        boolean hasCannon = buf.readBoolean();
        String shellName = buf.readUtf();
        double currentRange = buf.readDouble();
        int loadedCharges = buf.readInt();
        int count = buf.readInt();
        int[] levels = new int[count];
        double[] ranges = new double[count];
        for (int i = 0; i < count; i++) {
            levels[i] = buf.readInt();
            ranges[i] = buf.readDouble();
        }
        return new ScopeRangeDataPacket(hasCannon, shellName, currentRange, loadedCharges, levels, ranges);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ScopeRangeDataPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleScopeRangeDataPacket(msg));
    }
}
