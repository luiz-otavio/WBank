package com.rededark.wbank.adapter.text;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TextAdapter {

    private static final AtomicReference<FileConfiguration> ATOMIC_REFERENCE = new AtomicReference<>();

    public static String accept(String key, OfflinePlayer player) {
        return ATOMIC_REFERENCE.get().getString(key);
    }

    public static List<String> accept(String key) {
        return ATOMIC_REFERENCE.get().getStringList(key);
    }

    public static void setReference(FileConfiguration fileConfiguration) {
        ATOMIC_REFERENCE.set(fileConfiguration);
    }

    public static ConfigurationSection getSection(String key) {
        final FileConfiguration fileConfiguration = ATOMIC_REFERENCE.get();

        if(!fileConfiguration.isConfigurationSection(key)) return null;

        return fileConfiguration.getConfigurationSection(key);
    }

}
