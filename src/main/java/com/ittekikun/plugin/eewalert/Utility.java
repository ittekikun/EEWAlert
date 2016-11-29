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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
     * HTTPサーバー上のテキストの内容を読み込む
     *
     * @param par1 URL
     * @return テキストをListで返す
     */
    /**
     * HTTPサーバー上のテキストの内容を読み込む
     *
     * @param par1 URL
     * @return テキストをListで返す
     */
    public static List getHttpServerText(String par1) throws IOException
    {
        URL url = new URL(par1);
        InputStream i = url.openConnection().getInputStream();

        BufferedReader buf = new BufferedReader(new InputStreamReader(i, "UTF-8"));

        String line;
        List<String> arrayList = new ArrayList();

        while ((line = buf.readLine()) != null)
        {
            arrayList.add(line);
        }
        buf.close();

        return arrayList;
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

    private static Boolean isCB19orLaterCache;
    /**
     * 現在動作中のCraftBukkitが、v1.9 以上かどうかを確認する
     * @author https://github.com/ucchyocean/
     * @return v1.9以上ならtrue、そうでないならfalse
     */
    public static boolean isCB19orLater()
    {
        if(isCB19orLaterCache == null)
        {
            isCB19orLaterCache = isUpperVersion(Bukkit.getBukkitVersion(), "1.9");
        }
        return isCB19orLaterCache;
    }

    /**
     * 指定されたバージョンが、基準より新しいバージョンかどうかを確認する
     * @author https://github.com/ucchyocean/
     * @param version 確認するバージョン
     * @param border 基準のバージョン
     * @return 基準より確認対象の方が新しいバージョンかどうか<br/>
     * ただし、無効なバージョン番号（数値でないなど）が指定された場合はfalseに、
     * 2つのバージョンが完全一致した場合はtrueになる。
     */
    private static boolean isUpperVersion(String version, String border)
    {
        int hyphen = version.indexOf("-");
        if ( hyphen > 0 ) {
            version = version.substring(0, hyphen);
        }

        String[] versionArray = version.split("\\.");
        int[] versionNumbers = new int[versionArray.length];
        for ( int i=0; i<versionArray.length; i++ ) {
            if ( !versionArray[i].matches("[0-9]+") )
                return false;
            versionNumbers[i] = Integer.parseInt(versionArray[i]);
        }

        String[] borderArray = border.split("\\.");
        int[] borderNumbers = new int[borderArray.length];
        for ( int i=0; i<borderArray.length; i++ ) {
            if ( !borderArray[i].matches("[0-9]+") )
                return false;
            borderNumbers[i] = Integer.parseInt(borderArray[i]);
        }

        int index = 0;
        while ( (versionNumbers.length > index) && (borderNumbers.length > index) ) {
            if ( versionNumbers[index] > borderNumbers[index] ) {
                return true;
            } else if ( versionNumbers[index] < borderNumbers[index] ) {
                return false;
            }
            index++;
        }
        if ( borderNumbers.length == index ) {
            return true;
        } else {
            return false;
        }
    }

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
            }
            else
            {
                Player[] temp = ((Player[])Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
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
}