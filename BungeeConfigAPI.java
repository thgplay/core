package com.thgplugins.guild.core;

import lombok.var;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigAPI {

    private final Plugin plugin;
    private Configuration configuration;
    private File file;

    public ConfigAPI(Plugin plugin, String configName) {
        this.plugin = plugin;
        var configFile = new File(plugin.getDataFolder(), configName);
        if (!configFile.exists()) {
            // Create config file if it doesn't exist
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.file = configFile;
        load();
    }

    public void load() {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String path) {
        return configuration.getString(path);
    }

    public String getString(String path, String def) {
        return configuration.getString(path, def);
    }

    public void setString(String path, String value) {
        configuration.set(path, value);
    }

    public int getInt(String path) {
        return configuration.getInt(path);
    }

    public int getInt(String path, int def) {
        return configuration.getInt(path, def);
    }

    public void setInt(String path, int value) {
        configuration.set(path, value);
    }

    public boolean getBoolean(String path) {
        return configuration.getBoolean(path);
    }

    public void setBoolean(String path, boolean value) {
        configuration.set(path, value);
    }

    public List<String> getStringList(String path) {
        return configuration.getStringList(path);
    }

    public long getLong(String path) {
        return configuration.getLong(path);
    }

    public long getLong(String path, long def) {
        return configuration.getLong(path, def);
    }

    public void setStringList(String path, List<String> value) {
        configuration.set(path, value);
    }

}
