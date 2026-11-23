package com.cta.item;

import com.cta.block.ScopeBlockEntity;
import com.cta.compat.CBCCompat;
import com.cta.network.OpenChannelScreenPacket;
import com.cta.network.PacketHandler;
import com.cta.wireless.WirelessLinkSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class WirelessConnectorItem extends Item {

    public WirelessConnectorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Nullable
    private static CompoundTag getCustomTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null ? data.copyTag() : null;
    }

    // ── Clear Tool Memory (Shift-Right-Click in Air) ─────────────────────────
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && !level.isClientSide()) {
            CompoundTag tag = getCustomTag(stack);
            if (tag != null && (tag.contains("ScopeBindPos") || tag.contains("PeripheralPos"))) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
                    t.remove("ScopeBindPos");
                    t.remove("ScopeBindDim");
                    t.remove("PeripheralPos");
                    t.remove("PeripheralDim");
                });
                player.sendOverlayMessage(Component.literal("Cleared selected connections from tool.").withStyle(ChatFormatting.YELLOW));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        String currentDim = level.dimension().identifier().toString();

        // ── INSPECT MODE (Right-Click without Sneaking) ──────────────────────
        if (!player.isShiftKeyDown()) {
            if (level.isClientSide()) return InteractionResult.SUCCESS;

            BlockEntity clickedBe = level.getBlockEntity(clickedPos);

            // Inspect Scope
            if (clickedBe instanceof ScopeBlockEntity scopeBE) {
                BlockPos bound = scopeBE.getBoundCannonMount();
                if (bound != null) {
                    player.sendOverlayMessage(Component.literal("Scope is bound to Cannon at " + bound.toShortString()).withStyle(ChatFormatting.AQUA));
                } else {
                    player.sendOverlayMessage(Component.literal("Scope is currently unbound.").withStyle(ChatFormatting.GRAY));
                }
                return InteractionResult.SUCCESS;
            }

            // Inspect CC Peripheral
            if (level instanceof ServerLevel serverLevel) {
                WirelessLinkSavedData data = WirelessLinkSavedData.get(serverLevel);
                if (data.isPeripheral(clickedPos)) {
                    player.sendOverlayMessage(Component.literal("This block is connected to the CC wireless network.").withStyle(ChatFormatting.GREEN));
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        }

        // ── DRIVE BY WIRE BINDING MODE (Shift-Right-Click Block) ─────────────
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity clickedBe = level.getBlockEntity(clickedPos);
        boolean isScope = clickedBe instanceof ScopeBlockEntity;
        boolean isCannon = CBCCompat.isCBCLoaded() && CBCCompat.isCannonMount(level, clickedPos);
        CompoundTag tag = getCustomTag(stack);

        // 1. SCOPE -> CANNON LOGIC
        if (isScope) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
                t.putLong("ScopeBindPos", clickedPos.asLong());
                t.putString("ScopeBindDim", currentDim);
                t.remove("PeripheralPos");
                t.remove("PeripheralDim");
            });
            player.sendOverlayMessage(Component.literal("Scope selected at " + clickedPos.toShortString()).withStyle(ChatFormatting.AQUA));
            return InteractionResult.SUCCESS;
        }

        if (isCannon && tag != null && tag.contains("ScopeBindPos")) {
            if (!currentDim.equals(tag.getStringOr("ScopeBindDim", ""))) {
                player.sendOverlayMessage(Component.literal("Scope and cannon must be in the same dimension!").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }
            BlockPos scopePos = BlockPos.of(tag.getLongOr("ScopeBindPos", 0L));
            if (!(level.getBlockEntity(scopePos) instanceof ScopeBlockEntity scopeBE)) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> t.remove("ScopeBindPos"));
                player.sendOverlayMessage(Component.literal("Selected scope no longer exists.").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }
            scopeBE.bindToCannonMount(clickedPos);
            CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
                t.remove("ScopeBindPos");
                t.remove("ScopeBindDim");
            });
            player.sendOverlayMessage(Component.literal("Scope bound to cannon at " + clickedPos.toShortString()).withStyle(ChatFormatting.GREEN));
            return InteractionResult.SUCCESS;
        }

        // 2. CC PERIPHERAL LOGIC
        if (tag != null && tag.contains("PeripheralPos")
                && BlockPos.of(tag.getLongOr("PeripheralPos", 0L)).equals(clickedPos)) {
            if (level instanceof ServerLevel serverLevel) {
                WirelessLinkSavedData data = WirelessLinkSavedData.get(serverLevel);
                int removed = data.removeAllLinksForPeripheral(clickedPos);
                CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
                    t.remove("PeripheralPos");
                    t.remove("PeripheralDim");
                });
                player.sendOverlayMessage(Component.literal("Removed " + removed + " links from this peripheral.").withStyle(ChatFormatting.RED));
            }
            return InteractionResult.SUCCESS;
        }

        if (tag == null || !tag.contains("PeripheralPos")) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
                t.putLong("PeripheralPos", clickedPos.asLong());
                t.putString("PeripheralDim", currentDim);
                t.remove("ScopeBindPos");
                t.remove("ScopeBindDim");
            });

            String blockName = BuiltInRegistries.BLOCK.getKey(level.getBlockState(clickedPos).getBlock()).getPath();
            player.sendOverlayMessage(Component.literal("Selected " + blockName + " as Source. Click target to link...").withStyle(ChatFormatting.GREEN));
            return InteractionResult.SUCCESS;
        } else {
            if (!currentDim.equals(tag.getStringOr("PeripheralDim", ""))) {
                player.sendOverlayMessage(Component.literal("Both blocks must be in the same dimension!").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }

            BlockPos peripheralPos = BlockPos.of(tag.getLongOr("PeripheralPos", 0L));
            String blockName = BuiltInRegistries.BLOCK.getKey(level.getBlockState(clickedPos).getBlock()).getPath();

            if (player instanceof ServerPlayer serverPlayer) {
                PacketHandler.sendToPlayer(serverPlayer, new OpenChannelScreenPacket(peripheralPos, clickedPos));
                player.sendOverlayMessage(Component.literal("Linking Source to " + blockName + "...").withStyle(ChatFormatting.GREEN));
            }

            CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
                t.remove("PeripheralPos");
                t.remove("PeripheralDim");
            });

            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = getCustomTag(stack);
        if (tag != null && tag.contains("ScopeBindPos")) {
            BlockPos pos = BlockPos.of(tag.getLongOr("ScopeBindPos", 0L));
            tooltip.accept(Component.literal("Scope Source: " + pos.toShortString()).withStyle(ChatFormatting.AQUA));
            tooltip.accept(Component.literal("Shift+click a Cannon Mount to finish bind").withStyle(ChatFormatting.YELLOW));
            tooltip.accept(Component.literal("Shift+click air to cancel").withStyle(ChatFormatting.DARK_GRAY));
        } else if (tag != null && tag.contains("PeripheralPos")) {
            BlockPos pos = BlockPos.of(tag.getLongOr("PeripheralPos", 0L));
            tooltip.accept(Component.literal("CC Source: " + pos.toShortString()).withStyle(ChatFormatting.GREEN));
            tooltip.accept(Component.literal("Shift+click target to connect").withStyle(ChatFormatting.YELLOW));
            tooltip.accept(Component.literal("Shift+click source again to disconnect").withStyle(ChatFormatting.RED));
            tooltip.accept(Component.literal("Shift+click air to cancel").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.accept(Component.literal("Shift+click blocks to create wireless links").withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.literal("Right-click a linked block to inspect it").withStyle(ChatFormatting.DARK_GRAY));
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flag);
    }
}
