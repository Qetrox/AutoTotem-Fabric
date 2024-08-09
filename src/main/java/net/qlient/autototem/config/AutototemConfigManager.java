package net.qlient.autototem.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AutototemConfigManager {

    private static final File CONFIG_FILE = new File("config/autototem.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static AutototemConfigData config;

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, AutototemConfigData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            config = new AutototemConfigData();
            saveConfig();
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AutototemConfigData getConfig() {
        return config;
    }
}
