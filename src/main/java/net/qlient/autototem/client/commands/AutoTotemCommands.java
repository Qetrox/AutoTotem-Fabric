package net.qlient.autototem.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.qlient.autototem.config.AutototemConfigBuilder;
import net.qlient.autototem.config.AutototemConfigManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AutoTotemCommands {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("autototem")
                .executes(ctx -> openConfig())
        );
    }

    private static int openConfig() {
        // Open the AutoTotem config GUI
        MinecraftClient client = MinecraftClient.getInstance();
        client.send(() -> client.setScreen(AutototemConfigBuilder.getConfigBuilder(client.currentScreen)));
        return Command.SINGLE_SUCCESS;
    }

}
