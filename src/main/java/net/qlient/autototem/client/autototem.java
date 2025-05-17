package net.qlient.autototem.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.qlient.autototem.config.AutototemConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class autototem implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("AutoTotem");
	public static final String VERSION = "2.0.2";

	@Override
	public void onInitializeClient() {
		AutototemConfigManager.loadConfig();
		LOGGER.info("Loaded!");
	}
}
