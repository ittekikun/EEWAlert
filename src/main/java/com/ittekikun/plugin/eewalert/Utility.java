package com.ittekikun.plugin.eewalert;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class Utility
{


    /**
     * ArrayUnion
     *
     * @param par1 繋げたい配列（配列String型）
     * @param par2 どこの配列から繋げたいか（int型）
     */
    public static String JoinArray(String[] par1, int par2)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (int a = par2; a < par1.length; ++a)
        {
            if (a > par2)
            {
                stringBuilder.append(" ");
            }

            String s = par1[a];

            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }

    /**
     * timeGetter
     *
     * @param format 出力する時刻のフォーマット（String）
     * @return 指定したフォーマットの形で現時刻
     * @author ittekikun
     */
    public static String timeGetter(String format)
    {
        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String time = sdf.format(date);

        return time;
    }

    public static String simpleTimeGetter()
    {
        Calendar calendar = Calendar.getInstance();
        String time = calendar.getTime().toString();

        return time;
    }


    /**
     * HTTPサーバー上のテキストの内容を読み込む
     *
     * @param par1 URL
     * @return テキストをListで返す
     */
    public static String[] getHttpServerText(String par1)
    {
        try
        {
            URL url = new URL(par1);
            InputStream i = url.openConnection().getInputStream();

            //いつかUTF8に対応したいなって（動作確認済み）
            //↓
            //1.4より移行
            BufferedReader buf = new BufferedReader(new InputStreamReader(i, "UTF-8"));

            //BufferedReader buf = new BufferedReader(new InputStreamReader(i));

            String line = null;
            int l = 0;
            //これスマートじゃないので修正予定
            String[] strarray = new String[1000];
            while ((line = buf.readLine()) != null)
            {
                strarray[l] = line;
                l++;
            }
            buf.close();
            return strarray;
        }
        catch (IOException e)
        {
            EEWAlert.log.severe("何らかの理由でバージョンアップ確認サーバーにアクセスできませんでした。");
            EEWAlert.log.severe("お手数ですが一度UpdateCheckをfalseにする事をおすすめします。");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * jarファイルの中に格納されているテキストファイルを、jarファイルの外にコピーするメソッド<br/>
     * ファイルをそのままコピーします。
     *
     * @param jarFile        jarファイル
     * @param targetFile     コピー先
     * @param sourceFilePath コピー元
     */
    public static void copyRawFileFromJar(File jarFile, File targetFile, String sourceFilePath)
    {
        JarFile jar = null;
        InputStream is = null;

        File parent = targetFile.getParentFile();
        if (!parent.exists())
        {
            parent.mkdirs();
        }

        try
        {
            jar = new JarFile(jarFile);
            ZipEntry zipEntry = jar.getEntry(sourceFilePath);
            is = jar.getInputStream(zipEntry);

            Files.copy(is, targetFile.toPath());
        }
        catch (IOException e)
        {
            EEWAlert.log.severe(targetFile + "のコピーに失敗しました。");
            e.printStackTrace();
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
        }
    }

    /**
     * jarファイルの中に格納されているフォルダを、中のファイルごとまとめてjarファイルの外にコピーするメソッド<br/>
     * テキストファイルは、そのままコピーされます。
     *
     * @param jarFile        jarファイル
     * @param targetFilePath コピー先のフォルダ
     * @param sourceFilePath コピー元のフォルダ
     * @author https://github.com/ucchyocean/
     */
    public static void copyRawFolderFromJar(File jarFile, File targetFilePath, String sourceFilePath)
    {

        JarFile jar = null;

        if (!targetFilePath.exists())
        {
            targetFilePath.mkdirs();
        }

        try
        {
            jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements())
            {

                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().startsWith(sourceFilePath))
                {

                    File targetFile = new File(targetFilePath, sourceFilePath);
                    if (!targetFile.getParentFile().exists())
                    {
                        targetFile.getParentFile().mkdirs();
                    }

                    if (!targetFile.exists())
                    {
                        targetFile.mkdir();
                    }

                    File target = new File(targetFile, entry.getName().substring(sourceFilePath.length() + 1));

                    InputStream is = null;

                    try
                    {
                        is = jar.getInputStream(entry);

                        Files.copy(is, target.toPath());
                    }
                    catch (FileNotFoundException e)
                    {
                        EEWAlert.log.severe(targetFilePath + "フォルダのコピーに失敗しました。");
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        EEWAlert.log.severe(targetFilePath + "フォルダのコピーに失敗しました。");
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (is != null)
                        {
                            try
                            {
                                is.close();
                            }
                            catch (IOException e)
                            {
                                // do nothing.
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
        }

    }

    /**
     * jarファイルの中に格納されているテキストファイルを、jarファイルの外にコピーするメソッド<br/>
     * WindowsだとS-JISで、MacintoshやLinuxだとUTF-8で保存されます。
     *
     * @param jarFile        jarファイル
     * @param targetFile     コピー先
     * @param sourceFilePath コピー元
     * @author https://github.com/ucchyocean/
     */
    public static void copyFileFromJar(File jarFile, File targetFile, String sourceFilePath)
    {
        JarFile jar = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        File parent = targetFile.getParentFile();
        if (!parent.exists())
        {
            parent.mkdirs();
        }

        try
        {
            jar = new JarFile(jarFile);
            ZipEntry zipEntry = jar.getEntry(sourceFilePath);
            is = jar.getInputStream(zipEntry);

            fos = new FileOutputStream(targetFile);

            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(fos));

            String line;
            while ((line = reader.readLine()) != null)
            {
                writer.write(line);
                writer.newLine();
            }

        }
        catch (FileNotFoundException e)
        {
            EEWAlert.log.severe(targetFile + "のコピーに失敗しました。");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            EEWAlert.log.severe(targetFile + "のコピーに失敗しました。");
            e.printStackTrace();
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
            if (writer != null)
            {
                try
                {
                    writer.flush();
                    writer.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.flush();
                    fos.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
        }
    }

    /**
     * jarファイルの中に格納されているフォルダを、中のファイルごとまとめてjarファイルの外にコピーするメソッド<br/>
     * テキストファイルは、WindowsだとS-JISで、MacintoshやLinuxだとUTF-8で保存されます。
     *
     * @param jarFile        jarファイル
     * @param targetFilePath コピー先のフォルダ
     * @param sourceFilePath コピー元のフォルダ
     * @author https://github.com/ucchyocean/
     */
    public static void copyFolderFromJar(File jarFile, File targetFilePath, String sourceFilePath)
    {

        JarFile jar = null;

        if (!targetFilePath.exists())
        {
            targetFilePath.mkdirs();
        }

        try
        {
            jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements())
            {

                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().startsWith(sourceFilePath))
                {

                    File targetFile = new File(targetFilePath, sourceFilePath);
                    if (!targetFile.getParentFile().exists())
                    {
                        targetFile.getParentFile().mkdirs();
                    }

                    if (!targetFile.exists())
                    {
                        targetFile.mkdir();
                    }

                    File target = new File(targetFile, entry.getName().substring(sourceFilePath.length() + 1));

                    InputStream is = null;
                    FileOutputStream fos = null;
                    BufferedReader reader = null;
                    BufferedWriter writer = null;

                    try
                    {
                        is = jar.getInputStream(entry);
                        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        fos = new FileOutputStream(target);
                        writer = new BufferedWriter(new OutputStreamWriter(fos));

                        String line;
                        while ((line = reader.readLine()) != null)
                        {
                            writer.write(line);
                            writer.newLine();
                        }

                    }
                    catch (FileNotFoundException e)
                    {
                        EEWAlert.log.severe(targetFilePath + "フォルダのコピーに失敗しました。");
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        EEWAlert.log.severe(targetFilePath + "フォルダのコピーに失敗しました。");
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (writer != null)
                        {
                            try
                            {
                                writer.flush();
                                writer.close();
                            }
                            catch (IOException e)
                            {
                                // do nothing.
                            }
                        }
                        if (reader != null)
                        {
                            try
                            {
                                reader.close();
                            }
                            catch (IOException e)
                            {
                                // do nothing.
                            }
                        }
                        if (fos != null)
                        {
                            try
                            {
                                fos.flush();
                                fos.close();
                            }
                            catch (IOException e)
                            {
                                // do nothing.
                            }
                        }
                        if (is != null)
                        {
                            try
                            {
                                is.close();
                            }
                            catch (IOException e)
                            {
                                // do nothing.
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
        }
    }

    /**
     * 文字列が整数値に変換可能かどうかを判定する
     *
     * @param source 変換対象の文字列
     * @return 整数に変換可能かどうか
     * @author https://github.com/ucchyocean/
     */
    public static boolean checkIntParse(String source)
    {

        return source.matches("^-?[0-9]{1,9}$");
    }


    //    /**
    //     * メッセージをユニキャスト
    //     *
    //     * @param message メッセージ
    //     */
    //    public static void message(CommandSender sender, String message)
    //    {
    //        if (sender != null && message != null)
    //        {
    //            sender.sendMessage(MineTweet.prefix + message.replaceAll("&([0-9a-fk-or])", "\u00A7$1"));
    //        }
    //    }
    //
    //    /**
    //     * メッセージをブロードキャスト
    //     *
    //     * @param message メッセージ
    //     */
    //    public static void broadcastMessage(String message)
    //    {
    //        if (message != null)
    //        {
    //            message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
    //            Bukkit.broadcastMessage(MineTweet.prefix + message);
    //        }
    //    }
    //
    //    /**
    //     * メッセージをワールドキャスト
    //     *
    //     * @param world
    //     * @param message
    //     */
    //    public static void worldcastMessage(World world, String message)
    //    {
    //        if (world != null && message != null)
    //        {
    //            message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
    //            for (Player player : world.getPlayers())
    //            {
    //                player.sendMessage(MineTweet.prefix + message);
    //            }
    //            MineTweet.log.info(MineTweet.prefix + "[Worldcast][" + world.getName() + "]: " + message);
    //        }
    //    }
    //
    //    /**
    //     * メッセージをパーミッションキャスト(指定した権限ユーザにのみ送信)
    //     *
    //     * @param permission 受信するための権限ノード
    //     * @param message    メッセージ
    //     */
    //    public static void permcastMessage(String permission, String message)
    //    {
    //        // OK
    //        int i = 0;
    //        for (Player player : Bukkit.getServer().getOnlinePlayers())
    //        {
    //            if (player.hasPermission(permission))
    //            {
    //                Utility.message(player, message);
    //                i++;
    //            }
    //        }
    //
    //        MineTweet.log.info(MineTweet.prefix + "Received " + i + "players: " + message);
    //    }
    //
    //    public static Player getPlayer(String name)
    //    {
    //        for (Player player : getOnlinePlayers())
    //        {
    //            if(player.getName().equals(name))
    //            {
    //                return player;
    //            }
    //        }
    //        return null;
    //    }

    /**
     * @return 接続中の全てのプレイヤー
     * @author https://github.com/ucchyocean/
     * 現在接続中のプレイヤーを全て取得する
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Player> getOnlinePlayers()
    {
        // CB179以前と、CB1710以降で戻り値が異なるため、
        // リフレクションを使って互換性を（無理やり）保つ。
        try
        {
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class)
            {
                Collection<?> temp = ((Collection<?>) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                return new ArrayList<Player>((Collection<? extends Player>) temp);
            } else
            {
                Player[] temp = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                ArrayList<Player> players = new ArrayList<Player>();
                for (Player t : temp)
                {
                    players.add(t);
                }
                return players;
            }
        }
        catch (NoSuchMethodException ex)
        {
            // never happen
        }
        catch (InvocationTargetException ex)
        {
            // never happen
        }
        catch (IllegalAccessException ex)
        {
            // never happen
        }
        return new ArrayList<Player>();
    }

    /**
     * 指定したプレイヤーが手に持っているアイテムを返します。
     * CB1.9以降と、CB1.8.8以前で、互換性を保つために使用します。
     *
     * @param player プレイヤー
     * @return 手に持っているアイテム
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getItemInHand(Player player)
    {
        if (EEWAlert.isV19)
        {
            return player.getInventory().getItemInMainHand();
        } else
        {
            return player.getItemInHand();
        }
    }

    /**
     * 短縮URL生成
     *
     * @param longUrl
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public static String getShortUrl(String longUrl, String apikey) throws ClientProtocolException, IOException
    {
        HttpPost post = new HttpPost("https://www.googleapis.com/urlshortener/v1/url?key=" + apikey);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity("{'longUrl': '"+longUrl+"'}", "UTF-8"));

        HttpResponse response = new DefaultHttpClient().execute(post);

        String responseText = EntityUtils.toString(response.getEntity());

        // JsonFactoryの生成
        JsonFactory factory = new JsonFactory();
        // JsonParserの取得
        @SuppressWarnings("deprecation")
        JsonParser parser = factory.createJsonParser(responseText);

        //JSONのパース処理
        String shotUrl = "";
        while (parser.nextToken() != JsonToken.END_OBJECT)
        {
            String name = parser.getCurrentName();
            if (name != null)
            {
                parser.nextToken();
                if (name.equals("id"))
                {
                    shotUrl = parser.getText();
                }
            }
        }
        return shotUrl;
    }

    public static APIKey decodeAPIKey(File file, String fileName) throws IOException, ClassNotFoundException
    {
        InputStream is;
        JarFile jar;

        jar = new JarFile(file);
        ZipEntry zipEntry = jar.getEntry(fileName);
        is = jar.getInputStream(zipEntry);

        byte[] indata = new byte[(int)zipEntry.getSize()];
        is.read(indata);
        is.close();

        byte[] outdata = Base64.decodeBase64(indata);

        ByteArrayInputStream bais = new ByteArrayInputStream(outdata);
        ObjectInputStream ois = new ObjectInputStream(bais);
        APIKey apiKey = (APIKey)ois.readObject();
        bais.close();
        ois.close();

        return apiKey;
    }
}