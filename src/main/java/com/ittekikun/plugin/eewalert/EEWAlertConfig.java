package com.ittekikun.plugin.eewalert;

public class EEWAlertConfig
{
    public EEWAlert eewAlert;
    public ConfigAccessor system;

    public boolean versionCheck;
    public boolean sendTitle;
    public boolean soundSE;
    public String soundSEName;
    public boolean notificationMode;
    public boolean demoMode;

    public EEWAlertConfig(EEWAlert eewAlert)
    {
        this.eewAlert = eewAlert;
    }

    public void loadConfig()
    {
        system = new ConfigAccessor(eewAlert, "system.yml");
        system.saveDefaultConfig();

        versionCheck = system.getConfig().getBoolean("VersionCheck", true);
        sendTitle = system.getConfig().getBoolean("SendTitle", false);
        soundSE  = system.getConfig().getBoolean("SoundSE", false);
        soundSEName = system.getConfig().getString("SoundSEName", "ENTITY_WITHER_SPAWN");
        notificationMode = system.getConfig().getBoolean("NotificationMode", true);
        demoMode = system.getConfig().getBoolean("DemoMode", false);
    }
}
