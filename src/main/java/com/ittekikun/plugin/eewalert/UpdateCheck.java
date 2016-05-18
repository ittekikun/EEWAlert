package com.ittekikun.plugin.eewalert;

import org.bukkit.entity.Player;

public class UpdateCheck implements Runnable
{
    Player player;
    String url;
    String nurl;
    String lastVer[];
    Double nowVer;

    String temp = "§e$Name§fの新しいバージョン§f(§6New=$LastVer§f,§cNow=$NowVer§f)が利用できます。§a$Reason";

    public UpdateCheck(Player player, String url, double nowVer)
    {
        this.player = player;
        this.url = url;
        this.nowVer = nowVer;
    }

    public void run()
    {
        nurl = "http://verche.ittekikun.com/" + url + "/lastver.txt";
        lastVer = Utility.getHttpServerText(nurl);
        Double ver = Double.valueOf(lastVer[1]);
        String name = lastVer[0];
        String reason = lastVer[2];

        if(ver > nowVer)
        {
            String message = replaceKeywords(temp, ver, nowVer,name,reason);
            player.sendMessage(message);
        }
    }

    private String replaceKeywords(String source,Double LastVer, Double NowVer, String name, String reason)
    {
        String result = source;
        if ( result.contains("$LastVer") )
        {
            result = result.replace("$LastVer", LastVer.toString());
        }
        if ( result.contains("$NowVer") )
        {
            result = result.replace("$NowVer", NowVer.toString());
        }
        if ( result.contains("$Name") )
        {
            result = result.replace("$Name", name);
        }
        if ( result.contains("$Reason") )
        {
            result = result.replace("$Reason", reason);
        }
        return result;
    }
}