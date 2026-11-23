package com.cta.client.screen;

import com.cta.network.PacketHandler;
import com.cta.network.SetChannelPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SetChannelScreen extends Screen {

    private final BlockPos peripheralPos;
    private final BlockPos targetPos;
    private EditBox channelInput;

    public SetChannelScreen(BlockPos peripheralPos, BlockPos targetPos) {
        super(Component.literal("Wireless Connector"));
        this.peripheralPos = peripheralPos;
        this.targetPos = targetPos;
    }

    public static void open(BlockPos peripheralPos, BlockPos targetPos) {
        Minecraft.getInstance().setScreenAndShow(new SetChannelScreen(peripheralPos, targetPos));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        channelInput = new EditBox(this.font, centerX - 75, centerY - 10, 150, 20, Component.literal("Channel"));
        channelInput.setMaxLength(32);
        channelInput.setValue("");
        this.addRenderableWidget(channelInput);
        this.setFocused(channelInput);

        this.addRenderableWidget(Button.builder(Component.literal("Connect"), button -> {
            sendAndClose();
        }).bounds(centerX - 50, centerY + 18, 100, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, "Wireless Connector", this.width / 2, this.height / 2 - 35, 0xFFFFFFFF);
        graphics.centeredText(this.font, "Channel Name:", this.width / 2, this.height / 2 - 23, 0xFFAAAAAA);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (channelInput.isFocused()) {
            return channelInput.charTyped(event);
        }
        return super.charTyped(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == 257) {
            sendAndClose();
            return true;
        }
        if (event.key() == 256) {
            this.onClose();
            return true;
        }
        if (channelInput.isFocused()) {
            return channelInput.keyPressed(event);
        }
        return super.keyPressed(event);
    }

    private void sendAndClose() {
        String name = channelInput.getValue().trim();
        if (!name.isEmpty()) {
            PacketHandler.sendToServer(new SetChannelPacket(peripheralPos, targetPos, name));
        }
        this.onClose();
    }
}
