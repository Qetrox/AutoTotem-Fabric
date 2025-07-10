package net.qlient.autototem.client;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.qlient.autototem.client.commands.AutoTotemCommands;
import net.qlient.autototem.config.AutototemConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class autototem implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("AutoTotem");
	public static final String VERSION = "2.1.0";

	@Override
	public void onInitializeClient() {
		AutototemConfigManager.loadConfig();
		ClientCommandRegistrationCallback.EVENT.register(autototem::registerCommands);

		LOGGER.info("Loaded!");
	}

	public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		AutoTotemCommands.register(dispatcher);
	}
}
