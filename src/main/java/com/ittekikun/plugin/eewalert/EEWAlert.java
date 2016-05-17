package com.ittekikun.plugin.eewalert;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static com.ittekikun.plugin.eewalert.Messenger.MessageType.SEVERE;
import static com.ittekikun.plugin.eewalert.Messenger.MessageType.WARNING;

public class EEWAlert  extends JavaPlugin
{
    public static EEWAlert instance;
    public static APIKey apiKey;
    public static Logger log;
    public static final String prefix = "[EEWAlert] ";
    public static PluginManager pluginManager;
    public static boolean forceDisableMode;
    public TwitterManager twitterManager;

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

        apiKey = loadAPIkey();

        twitterManager = new TwitterManager(this);
        twitterManager.startSetup();
    }

    public APIKey loadAPIkey()
    {
        try
        {
            return Utility.decodeAPIKey(getPluginJarFile(), "apikey");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        AccessToken accessToken;

        if (cmd.getName().equalsIgnoreCase("eew"))
        {
            if (args[0].equalsIgnoreCase("pin"))
            {
                if(twitterManager.canAuth)
                {
                    if(Utility.checkIntParse(args[1]))
                    {
                        if(args[1].length() == 7)
                        {
                            try
                            {
                                accessToken = twitterManager.getAccessToken(args[1]);

                                twitterManager.storeAccessToken(accessToken);
                                twitterManager.startSetup();
                            }
                            catch (TwitterException e)
                            {
                                e.printStackTrace();
                                Messenger.messageToSender(sender, SEVERE, "正しく認証されませんでした。");
                                Messenger.messageToSender(sender, SEVERE, "お手数ですがもう一度お試し下さい。");
                            }
                            return true;
                        }
                        else
                        {
                            Messenger.messageToSender(sender, WARNING, "PINコードが正しく入力されていません。(数値が8字以上入力されています。)");
                            return false;
                        }
                    }
                    else
                    {
                        Messenger.messageToSender(sender, WARNING, "PINコードが正しく入力されていません。(整数値に変換できません。)");
                        return false;
                    }
                }
                else
                {
                    Messenger.messageToSender(sender, WARNING, "このコマンドは現在実行できません。(認証時のみ使用)");
                    return false;
                }
            }
            else if(args[0].equalsIgnoreCase("test"))
            {
                if(twitterManager.canTweet)
                {
                    try
                    {
                        twitterManager.twitter.updateStatus(Utility.JoinArray(args,1));
                        return true;
                    }
                    catch (TwitterException e)
                    {
                        e.printStackTrace();
                        return false;
                    }
                }
                else
                {
                    Messenger.messageToSender(sender, WARNING, "このコマンドは現在実行できません。(認証されていません。)");
                    return false;
                }
            }
            else
            {
                Messenger.messageToSender(sender, WARNING, "引数がありません。");
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDisable()
    {
        twitterManager.shutdownRecieveStream();

        if(forceDisableMode)
        {
            return;
        }
    }

    public File getPluginJarFile()
    {
        return this.getFile();
    }
}