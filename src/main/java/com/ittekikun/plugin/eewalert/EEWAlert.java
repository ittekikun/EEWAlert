package com.ittekikun.plugin.eewalert;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class EEWAlert  extends JavaPlugin
{
    public static EEWAlert instance;
    public static Logger log;
    public static final String prefix = "[EEWAlert] ";
    public static PluginManager pluginManager;
    public static boolean forceDisableMode;


    public static boolean isV19;

    @Override
    public void onEnable()
    {
        String ver = getServer().getBukkitVersion();
        //念のために1.9.5まで拾えるように
        isV19 = (ver.startsWith("1.9-R") || ver.startsWith("1.9.1-R") || ver.startsWith("1.9.2-R") || ver.startsWith("1.9.3-R") || ver.startsWith("1.9.4-R") || ver.startsWith("1.9.5-R"));

        instance = this;
        pluginManager = instance.getServer().getPluginManager();

        log = Logger.getLogger("EEWAlert");
        log.setFilter(new LogFilter(prefix));

        if(!(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.7))
        {
            //JAVA6以前の環境では動きません
            log.severe("JAVA7以上がインストールされていません。");
            log.severe("プラグインを無効化します。");
            forceDisableMode = true;
            pluginManager.disablePlugin(this);

            return;
        }

    }

    @Override
    public void onDisable()
    {
        if(forceDisableMode)
        {
            return;
        }
    }
}