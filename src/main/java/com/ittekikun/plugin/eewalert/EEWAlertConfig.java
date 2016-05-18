package com.ittekikun.plugin.eewalert;

public class EEWAlertConfig
{
    public EEWAlert eewAlert;
    public ConfigAccessor system;

    public boolean versionCheck;
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
        demoMode = system.getConfig().getBoolean("DemoMode", false);
    }
}
