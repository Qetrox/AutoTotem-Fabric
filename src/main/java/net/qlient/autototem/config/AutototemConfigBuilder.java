package net.qlient.autototem.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

public class AutototemConfigBuilder {

    public static Screen getConfigBuilder(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("AutoTotem Config"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));
        ConfigCategory delay = builder.getOrCreateCategory(Text.of("Delay"));

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable AutoTotem"), AutototemConfigManager.getConfig().Enabled)
                .setTooltip(Text.of("Enable or disable the mod"))
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> AutototemConfigManager.getConfig().Enabled = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Check For Effects"), AutototemConfigManager.getConfig().CheckForEffects)
                .setTooltip(Text.of("Requires having fire resistance and regeneration before supplying a new totem."))
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> AutototemConfigManager.getConfig().CheckForEffects = newValue)
                .build());

        delay.addEntry(entryBuilder.startIntField(Text.of("Delay In Milliseconds"), AutototemConfigManager.getConfig().DelayInMilliseconds)
                .setTooltip(Text.of("Delay between your totem being used and a new one automatically being equipped."))
                .setDefaultValue(0)
                .setSaveConsumer(newValue -> AutototemConfigManager.getConfig().DelayInMilliseconds = newValue)
                .build());
        delay.addEntry(entryBuilder.startBooleanToggle(Text.of("Add Random Delay"), AutototemConfigManager.getConfig().AddRandomDelay)
                .setTooltip(Text.of("Adds an extra random delay between 0 and the Max Random Delay."))
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> AutototemConfigManager.getConfig().AddRandomDelay = newValue)
                .build());
        delay.addEntry(entryBuilder.startIntSlider(Text.of("Delay In Milliseconds"), AutototemConfigManager.getConfig().MaxRandomDelay, 0, 1000)
                .setTooltip(Text.of("The maximum random delay that can be added."))
                .setDefaultValue(500)
                .setSaveConsumer(newValue -> AutototemConfigManager.getConfig().MaxRandomDelay = newValue)
                .build());

        builder.setSavingRunnable(AutototemConfigManager::saveConfig);

        // Return the built screen
        return builder.build();
    }
}
