package com.ittekikun.plugin.eewalert;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigAccessor
{
    private final String fileName;
    private final JavaPlugin plugin;

    private File configFile;
    private FileConfiguration fileConfiguration;

    public ConfigAccessor(JavaPlugin plugin, String fileName)
    {
        if(plugin == null)
        {
            throw new IllegalArgumentException("plugin cannot be null");
        }
        //代替メソッドわからない
        if(!plugin.isInitialized())
        {
            throw new IllegalArgumentException("plugin must be initialized");
        }
        this.plugin = plugin;
        this.fileName = fileName;
        File dataFolder = plugin.getDataFolder();
        if(dataFolder == null)
        {
            throw new IllegalStateException();
        }
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }

    public void reloadConfig()
    {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig()
    {
        if(fileConfiguration == null)
        {
            this.reloadConfig();
        }
        return fileConfiguration;
    }

    public void saveConfig()
    {
        if(fileConfiguration == null || configFile == null)
        {
            return;
        }
        else
        {
            try
            {
                getConfig().save(configFile);
            }
            catch(IOException ex)
            {
                EEWAlert.log.severe("Could not save config to " + configFile);
                ex.printStackTrace();
            }
        }
    }

    public void saveDefaultConfig()
    {
        if(!configFile.exists())
        {
            //1.9のBUKKITAPIの仕様変更によりコピー処理を分ける必要がある。
            if(EEWAlert.isV19)
            {
                Utility.copyRawFileFromJar(EEWAlert.instance.getPluginJarFile(), configFile, fileName);
            }
            else
            {
                Utility.copyFileFromJar(EEWAlert.instance.getPluginJarFile(), configFile, fileName);
            }
        }
    }
}