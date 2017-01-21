package com.ittekikun.plugin.eewalert;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.ittekikun.plugin.eewalert.EEW.AlarmType.GENERAL;
import static com.ittekikun.plugin.eewalert.Messenger.MessageType.*;

public class EEWAlert  extends JavaPlugin
{
    public  EEWAlert instance;
    public APIKey apiKey;
    public static Logger log;
    public static final String prefix = "[EEWAlert] ";
    public  PluginManager pluginManager;
    public static boolean forceDisableMode;
    public TwitterManager twitterManager;
    public EEWAlertConfig eewAlertConfig;

    public static ArrayList<String> notifiedEEWIDList = new ArrayList<>();

    public TitleSender titleSender;

    @Override
    public void onEnable()
    {
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
        eewAlertConfig = new EEWAlertConfig(this);
        eewAlertConfig.loadConfig();

        if(eewAlertConfig.versionCheck)
        {
            pluginManager.registerEvents(new VersionCheckListener(this), this);
        }

        apiKey = loadAPIkey();

        twitterManager = new TwitterManager(this);
        twitterManager.startSetup();

        if(eewAlertConfig.sendTitle)
        {
            titleSender = new TitleSender();
            log.info("[BETA]緊急地震速報をTitleコマンドで通知する機能を有効にしました。(SendTitle)");
        }

        if(eewAlertConfig.soundSE)
        {
            boolean soundNameExists = false;

            Class<?> cl = Sound.class;
            for (Object o: cl.getEnumConstants())
            {
                if(o.toString().equals(eewAlertConfig.soundSEName))
                {
                    soundNameExists = true;
                    log.info("[BETA]緊急地震速報を音で通知する機能を有効にしました。(SoundSE)");
                    break;
                }
            }

            if(!soundNameExists)
            {
                log.warning("設定されているSE「"+ eewAlertConfig.soundSEName + "」が存在しないのでSoundSE設定を無効化します。");
                eewAlertConfig.soundSE = false;
                return;
            }
        }
    }

    public APIKey loadAPIkey()
    {
        try
        {
            return Utility.decodeAPIKey(getPluginJarFile(), "imas");
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
        if (cmd.getName().equalsIgnoreCase("eew"))
        {
            if(args.length == 0)
            {
                Messenger.messageToSender(sender, WARNING, "引数がありません。");
                help(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("pin"))
            {
                if(checkPermission(sender, "eewalert.pin"))
                {
                    if(twitterManager.canAuth)
                    {
                        if(args.length == 2)
                        {
                            if(Utility.checkIntParse(args[1]))
                            {
                                if(args[1].length() == 7)
                                 {
                                    AccessToken accessToken;
                                    try
                                    {
                                        accessToken = twitterManager.getAccessToken(args[1]);

                                        twitterManager.storeAccessToken(accessToken);
                                        Messenger.messageToSender(sender, INFO, "Twitterと正しく認証されました。");
                                        twitterManager.startSetup();
                                    }
                                    catch (TwitterException e)
                                    {
                                        e.printStackTrace();
                                        Messenger.messageToSender(sender, SEVERE, "正しく認証されませんでした。(PINコードが使えなかった。もしくは無効になっている。)");
                                        Messenger.messageToSender(sender, SEVERE, "お手数ですがもう一度お試し下さい。");
                                    }
                                    return true;
                                }
                                else
                                {
                                    Messenger.messageToSender(sender, WARNING, "PINコードが正しく入力されていません。(正しいPINコードの桁数は7です。)");
                                    return true;
                                }
                            }
                            else
                            {
                                Messenger.messageToSender(sender, WARNING, "PINコードが正しく入力されていません。(整数値に変換できません。)");
                                return true;
                            }
                        }
                        else
                        {
                            Messenger.messageToSender(sender, WARNING, "PINコードが正しく入力されていません。(コマンド構文が間違ってます。)");
                            return true;
                        }
                    }
                    else
                    {
                        Messenger.messageToSender(sender, WARNING, "このコマンドは現在実行できません。(認証時のみ使用)");
                        return true;
                    }
                }
                else
                {
                    Messenger.messageToSender(sender, WARNING, "そのコマンドを実行する権限がありません。");
                    return true;
                }
            }
            else if(args[0].equalsIgnoreCase("tw") || args[0].equalsIgnoreCase("tweet"))
            {
                if(checkPermission(sender, "eewalert.tweet"))
                {
                    if(twitterManager.canTweet)
                    {
                        if(args.length >= 2)
                        {
                            try
                            {
                                twitterManager.twitter.updateStatus(Utility.JoinArray(args, 1));
                                Messenger.messageToSender(sender, INFO, "ツイートに成功しました。");
                                return true;
                            }
                            catch (TwitterException e)
                            {
                                e.printStackTrace();
                                Messenger.messageToSender(sender, SEVERE, "何らかの理由でツイートに失敗しました。");
                                return true;
                            }
                        }
                        else
                        {
                            Messenger.messageToSender(sender, WARNING, "ツイートする文章が含まれていません。");
                        }
                    }
                    else
                    {
                        Messenger.messageToSender(sender, WARNING, "このコマンドは現在実行できません。(未認証)");
                    }
                }
                else
                {
                    Messenger.messageToSender(sender, WARNING, "そのコマンドを実行する権限がありません。");
                    return true;
                }
            }
            else if(args[0].equalsIgnoreCase("reload"))
            {
                if(checkPermission(sender, "eewalert.reload"))
                {
                    HandlerList.unregisterAll(this);

                    eewAlertConfig.loadConfig();

                    if(eewAlertConfig.versionCheck)
                    {
                        pluginManager.registerEvents(new VersionCheckListener(this), this);
                    }

                    if(eewAlertConfig.sendTitle)
                    {
                        titleSender = new TitleSender();
                    }

                    if(eewAlertConfig.soundSE)
                    {
                        boolean soundNameExists = false;

                        Class<?> cl = Sound.class;
                        for (Object o: cl.getEnumConstants())
                        {
                            if(o.toString().equals(eewAlertConfig.soundSEName))
                            {
                                soundNameExists = true;
                                break;
                            }
                        }

                        if(!soundNameExists)
                        {
                            Messenger.messageToSender(sender, WARNING, "設定されているSE「"+ eewAlertConfig.soundSEName + "」が存在しないのでSoundSE設定を無効化します。");
                            eewAlertConfig.soundSE = false;
                        }
                    }

                    Messenger.messageToSender(sender, INFO, "Configファイルをリロードしました。");
                    return true;
                }
                else
                {
                    Messenger.messageToSender(sender, WARNING, "そのコマンドを実行する権限がありません。");
                    return true;
                }
            }
            else if(args[0].equalsIgnoreCase("help"))
            {
                help(sender);
                return true;
            }
            else if((args[0].equalsIgnoreCase("test")))
            {

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        if(eewAlertConfig.soundSE)
                        {
                            for(final Player player : Utility.getOnlinePlayers())
                            {
                                Class<?> cl = Sound.class;

                                for (Object o: cl.getEnumConstants())
                                {
                                    if (o.toString().equals(eewAlertConfig.soundSEName))
                                    {
                                        player.playSound(player.getLocation(), (Sound)o, 10, 1);
                                    }
                                }
                            }
                        }
                    }
                }.runTaskAsynchronously(this);
                return true;
            }
            else
            {
                help(sender);
                return true;
            }
        }
        return false;
    }

    public void help(CommandSender sender)
    {
        Messenger.messageToSender(sender, INFO, "---------------ヘルプコマンド---------------");
        Messenger.messageToSender(sender, INFO, "現在使えるコマンドは以下の通りです。");
        Messenger.messageToSender(sender, INFO, "/eew pin <pin>    ※認証時のみ使用します。");
        Messenger.messageToSender(sender, INFO, "/eew reload       ※設定ファイルを再読み込みします。");
        Messenger.messageToSender(sender, INFO, "/eew tweet <text> ※textの内容をツイートします。");
        Messenger.messageToSender(sender, INFO, "/eew help         ※helpを表示します。");
    }

    public boolean checkPermission(CommandSender sender, String permission)
    {
        if(sender instanceof Player)
        {
            Player player = (Player)sender;
            if(player.hasPermission(permission) || player.isOp())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        //コンソールは管理者扱い
        else
        {
            return true;
        }
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

    public void noticeEewMessage(final EEW eew)
    {
        List<String> eewMes = new ArrayList<String>();

        //通知済か判定
        boolean notified = (!(notifiedEEWIDList.lastIndexOf(eew.getEewArray()[5]) == -1));

        if(eew.alarmType == GENERAL)
        {
            if(!notified && !eew.isRetweet())
            {
                eewMes.add(ChatColor.RED +    "----------緊急地震速報----------");

                eewMes.add(ChatColor.YELLOW + "発表時刻: " + ChatColor.WHITE + eew.getOccurrenceTime());
                eewMes.add(ChatColor.YELLOW + "震源地: " + ChatColor.WHITE + eew.getEpicenter());
                eewMes.add(ChatColor.YELLOW + "マグニチュード: " + ChatColor.WHITE + eew.getMagnitude());
                eewMes.add(ChatColor.YELLOW + "深さ: " + ChatColor.WHITE + eew.getDepth()+ "km");
                eewMes.add(ChatColor.YELLOW + "最大震度: " + ChatColor.WHITE + eew.getMaxScale());
                if(eew.focusType == EEW.FocusType.LAND)
                {
                    eewMes.add(ChatColor.YELLOW + "震源海陸判定: " + ChatColor.WHITE + "陸");
                }
                else if(eew.focusType == EEW.FocusType.SEA)
                {
                    eewMes.add(ChatColor.YELLOW + "震源海陸判定: " + ChatColor.RED + "海");
                }

                eewMes.add(ChatColor.RED +    "震源地付近にお住まいの方は大きな地震に注意してください。");
                if(eew.focusType == EEW.FocusType.SEA)
                {
                    eewMes.add(ChatColor.YELLOW +    "※※※震源が海の為、津波が発生する可能性があります。※※※");

                }
                eewMes.add(ChatColor.RED +    "この情報を鵜呑みにせず、テレビ・ラジオ等で正確な情報を収集してください。");
                eewMes.add(ChatColor.RED +    "※この情報は震度速報ではありません。あくまでも、地震の規模を早期に推定するものです。");

                eewMes.add(ChatColor.RED +    "--------------------------------");

                //追加機能
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        final ArrayList<Player> players = Utility.getOnlinePlayers();

                        if(eewAlertConfig.sendTitle)
                        {
                            for(Player player : players)
                            {
                                titleSender.setTime_second(player,0, 20, 0);
                                titleSender.sendTitle(player, "§c緊急地震速報が発表されました", "§e" + eew.getEpicenter() + "付近にお住まいの方は強い揺れに注意して下さい");
                            }
                        }

                        if(eewAlertConfig.soundSE)
                        {
                            for(final Player player : players)
                            {
                                Class<?> cl = Sound.class;

                                for (Object o: cl.getEnumConstants())
                                {
                                    if (o.toString().equals(eewAlertConfig.soundSEName))
                                    {
                                        player.playSound(player.getLocation(), (Sound)o, 10, 1);
                                    }
                                }
                            }
                        }
                    }
                }.runTaskAsynchronously(this);

                EEWAlert.log.info("緊急地震速報を受信しました。");

                //通知済リストへ追加
                notifiedEEWIDList.add(eew.getEewArray()[5]);
            }
            else if(eew.isRetweet() && eewAlertConfig.demoMode)
            {
                eewMes.add(ChatColor.RED +    "----------緊急地震速報(動作確認モード有効中)----------");

                eewMes.add(ChatColor.RED +    "テレビなどで普段発表されていない緊急地震速報を表示しています。");
                eewMes.add(ChatColor.RED +    "過去に発表された緊急地震速報を表示している可能性があります。");
                eewMes.add(ChatColor.RED +    "念の為、テレビ・ラジオ等で正確な情報を収集してください。");
                eewMes.add(ChatColor.RED +    "動作確認出来し次第、動作確認モードを無効にして下さい。");

                eewMes.add(ChatColor.RED + "※これはリツイートされたツイートを表示しています");

                eewMes.add(ChatColor.YELLOW + "発表時刻: " + ChatColor.WHITE + eew.getOccurrenceTime());
                eewMes.add(ChatColor.YELLOW + "震源地: " + ChatColor.WHITE + eew.getEpicenter());
                eewMes.add(ChatColor.YELLOW + "マグニチュード: " + ChatColor.WHITE + eew.getMagnitude());
                eewMes.add(ChatColor.YELLOW + "深さ: " + ChatColor.WHITE + eew.getDepth() + "km");
                eewMes.add(ChatColor.YELLOW + "最大震度: " + ChatColor.WHITE + eew.getMaxScale());
                if(eew.focusType == EEW.FocusType.LAND)
                {
                    eewMes.add(ChatColor.YELLOW + "震源海陸判定: " + ChatColor.WHITE + "陸");
                }
                else if(eew.focusType == EEW.FocusType.SEA)
                {
                    eewMes.add(ChatColor.YELLOW + "震源海陸判定: " + ChatColor.RED + "海");
                }

                if(eew.focusType == EEW.FocusType.SEA)
                {
                    eewMes.add(ChatColor.YELLOW +    "※※※震源が海の為、津波が発生する可能性があります。※※※");

                }

                eewMes.add(ChatColor.RED +    "※この情報は震度速報ではありません。あくまでも、地震の規模を早期に推定するものです。");

                eewMes.add(ChatColor.RED +    "--------------------------------");

                //追加機能
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        final ArrayList<Player> players = Utility.getOnlinePlayers();

                        if(eewAlertConfig.sendTitle)
                        {
                            for(Player player : players)
                            {
                                titleSender.setTime_second(player,0, 20, 0);
                                titleSender.sendTitle(player, "(動作確認モード有効中)§c緊急地震速報が発表されました", "§e" + eew.getEpicenter() + "付近にお住まいの方は強い揺れに注意して下さい");
                            }
                        }

                        if(eewAlertConfig.soundSE)
                        {
                            for(final Player player : players)
                            {
                                Class<?> cl = Sound.class;

                                for (Object o: cl.getEnumConstants())
                                {
                                    if (o.toString().equals(eewAlertConfig.soundSEName))
                                    {
                                        player.playSound(player.getLocation(), (Sound)o, 10, 1);
                                    }
                                }
                            }
                        }
                    }
                }.runTaskAsynchronously(this);

                EEWAlert.log.info("緊急地震速報を受信しました。(動作確認モード)");
            }
        }
        else if(eewAlertConfig.demoMode)
        {
            eewMes.add(ChatColor.RED +    "----------緊急地震速報(動作確認モード有効中)----------");

            eewMes.add(ChatColor.RED +    "テレビなどで普段発表されていない緊急地震速報を表示しています。");
            eewMes.add(ChatColor.RED +    "過去に発表された緊急地震速報を表示している可能性があります。");
            eewMes.add(ChatColor.RED +    "念の為、テレビ・ラジオ等で正確な情報を収集してください。");
            eewMes.add(ChatColor.RED +    "動作確認出来し次第、動作確認モードを無効にして下さい。");

            if(eew.isRetweet())
            {
                eewMes.add(ChatColor.RED + "※これはリツイートされたツイートを表示しています");
            }

            eewMes.add(ChatColor.YELLOW + "発表時刻: " + ChatColor.WHITE + eew.getOccurrenceTime());
            eewMes.add(ChatColor.YELLOW + "震源地: " + ChatColor.WHITE + eew.getEpicenter());
            eewMes.add(ChatColor.YELLOW + "マグニチュード: " + ChatColor.WHITE + eew.getMagnitude());
            eewMes.add(ChatColor.YELLOW + "深さ: " + ChatColor.WHITE + eew.getDepth() + "km");
            eewMes.add(ChatColor.YELLOW + "最大震度: " + ChatColor.WHITE + eew.getMaxScale());
            if(eew.focusType == EEW.FocusType.LAND)
            {
                eewMes.add(ChatColor.YELLOW + "震源海陸判定: " + ChatColor.WHITE + "陸");
            }
            else if(eew.focusType == EEW.FocusType.SEA)
            {
                eewMes.add(ChatColor.YELLOW + "震源海陸判定: " + ChatColor.RED + "海");
            }

            eewMes.add(ChatColor.RED +    "※この情報は震度速報ではありません。あくまでも、地震の規模を早期に推定するものです。");

            eewMes.add(ChatColor.RED +    "--------------------------------");

            //追加機能
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    final ArrayList<Player> players = Utility.getOnlinePlayers();

                    if(eewAlertConfig.sendTitle)
                    {
                        for(Player player : players)
                        {
                            titleSender.setTime_second(player,0, 20, 0);
                            titleSender.sendTitle(player, "(動作確認モード有効中)§c緊急地震速報が発表されました", "§e" + eew.getEpicenter() + "付近にお住まいの方は強い揺れに注意して下さい");
                        }
                    }

                    if(eewAlertConfig.soundSE)
                    {
                        for(final Player player : players)
                        {
                            Class<?> cl = Sound.class;

                            for (Object o: cl.getEnumConstants())
                            {
                                if (o.toString().equals(eewAlertConfig.soundSEName))
                                {
                                    player.playSound(player.getLocation(), (Sound)o, 10, 1);
                                }
                            }
                        }
                    }
                }
            }.runTaskAsynchronously(this);

            EEWAlert.log.info("緊急地震速報を受信しました。(動作確認モード)");
        }
        else
        {
            return;
        }

        broadcastMessage(eewMes);
    }

    public void broadcastMessage(final List eewMes)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for(int i = 0; i < eewMes.size(); ++i)
                {
                    Bukkit.broadcastMessage(eewMes.get(i).toString());
                }
            }
        }.runTaskAsynchronously(this);
    }
}