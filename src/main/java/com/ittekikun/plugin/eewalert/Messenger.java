package com.ittekikun.plugin.eewalert;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.ittekikun.plugin.eewalert.Messenger.MessageType.*;

public class Messenger
{
    public static void messageToSender(CommandSender sender, MessageType messageType, String message)
    {
        if(messageType == INFO)
        {
            if (sender instanceof Player)
            {
                sender.sendMessage(EEWAlert.prefix + ChatColor.AQUA + "[情報] " + ChatColor.WHITE + message);
            } else
            {
                EEWAlert.log.info(message);
            }
        }
         else if(messageType == WARNING)
        {
            if (sender instanceof Player)
            {
                sender.sendMessage(EEWAlert.prefix + ChatColor.YELLOW + "[警告] " + ChatColor.WHITE + message);
            } else
            {
                EEWAlert.log.warning(message);
            }
        }
        else if(messageType == SEVERE)
        {
            if (sender instanceof Player)
            {
                sender.sendMessage(EEWAlert.prefix + ChatColor.RED + "[重大] " + ChatColor.WHITE + message);
            }
            else
            {
                EEWAlert.log.severe(message);
            }
        }
    }

    public enum MessageType
    {
        INFO,
        WARNING,
        SEVERE
    }
}