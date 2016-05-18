package com.ittekikun.plugin.eewalert;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VersionCheckListener implements Listener
{
    EEWAlert plugin;
    EEWAlertConfig eewAlertConfig;

    public VersionCheckListener(EEWAlert plugin)
    {
        this.plugin = plugin;
        this.eewAlertConfig = plugin.eewAlertConfig;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if(player.isOp() || player.hasPermission("eewalert.vcheck"))
        {
            double NowVer = Double.valueOf(plugin.getServer().getPluginManager().getPlugin("EEWAlert").getDescription().getVersion());

            //いつかUTF8に対応したいなって（動作確認済み）
            //↓
            //1.4より移行
            String url = "EEWAlert";

            Thread updateCheck = new Thread(new UpdateCheck(player,url,NowVer));
            updateCheck.start();
        }
    }
}