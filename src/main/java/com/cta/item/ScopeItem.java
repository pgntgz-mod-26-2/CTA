package com.cta.item;

import com.cta.registry.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ScopeItem extends BlockItem {

    public ScopeItem(Properties properties) {
        super(ModBlocks.SCOPE_BLOCK.get(), properties.useBlockDescriptionPrefix());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.cta.scope.place").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.cta.scope.use").withStyle(ChatFormatting.YELLOW));
        tooltip.accept(Component.literal("Use the Wireless Connector to bind to a cannon mount").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.cta.scope.pickup").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flag);
    }
}
