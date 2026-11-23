package com.cta.command;

import com.cta.CTA;
import com.cta.client.debug.MissileDebugRenderer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = CTA.MODID)
public class CTADebugCommand {

    private static boolean debugEnabled = false;

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
        if (!enabled && FMLEnvironment.getDist().isClient()) {
            MissileDebugRenderer.clearAll();
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("cta")
                .then(Commands.literal("debug")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                    .executes(context -> {
                        debugEnabled = !debugEnabled;
                        context.getSource().sendSuccess(() ->
                            Component.literal("CTA Debug mode: " + (debugEnabled ? "§aON" : "§cOFF")), true);
                        if (!debugEnabled && FMLEnvironment.getDist().isClient()) {
                            MissileDebugRenderer.clearAll();
                        }
                        return 1;
                    })
                    .then(Commands.argument("enabled", BoolArgumentType.bool())
                        .executes(context -> {
                            debugEnabled = BoolArgumentType.getBool(context, "enabled");
                            context.getSource().sendSuccess(() ->
                                Component.literal("CTA Debug mode: " + (debugEnabled ? "§aON" : "§cOFF")), true);
                            if (!debugEnabled && FMLEnvironment.getDist().isClient()) {
                                MissileDebugRenderer.clearAll();
                            }
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("clear")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                    .executes(context -> {
                        if (FMLEnvironment.getDist().isClient()) {
                            MissileDebugRenderer.clearAll();
                        }
                        context.getSource().sendSuccess(() ->
                            Component.literal("§eCleared all debug visualizations"), true);
                        return 1;
                    })
                )
        );
    }
}
