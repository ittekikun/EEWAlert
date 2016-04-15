package com.ittekikun.plugin.eewalert;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class TwitterManager
{
    public EEWAlert eewAlert;
    public Twitter twitter;
    public TwitterStream eewStream;
    //public MineTweetConfig mtConfig;
    public AccessToken accesstoken;

    public TwitterManager(EEWAlert eewAlert)
    {
        eewAlert = this.eewAlert;
    }

    public void startSetup()
    {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.setOAuthConsumerKey(ConsumerKey.m_ConsumerKey);
        builder.setOAuthConsumerSecret(ConsumerKey.m_ConsumerSecret);
        Configuration conf = builder.build();

        twitter = new TwitterFactory(conf).getInstance();
        eewStream = new TwitterStreamFactory(conf).getInstance();

        accesstoken = loadAccessToken();

        //初期起動時(ファイルなし)
        if(accesstoken == null)
        {

        }
        //ファイル有り
        else
        {
            twitter.setOAuthAccessToken(accesstoken);
            eewStream.setOAuthAccessToken(accesstoken);
        }
    }

    public URL createOAuthUrl()
    {
        RequestToken requestToken = null;
        URL url = null;

        try
        {
            requestToken = twitter.getOAuthRequestToken();
            url = new URL(requestToken.getAuthorizationURL());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return url;
    }

    public AccessToken loadAccessToken()
    {
        File f = createAccessTokenFileName();

        ObjectInputStream is = null;
        try
        {
            is = new ObjectInputStream(new FileInputStream(f));
            AccessToken accessToken = (AccessToken)is.readObject();
            return accessToken;
        }
        catch (IOException e)
        {
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            if(is != null){
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public AccessToken getAccessToken(RequestToken  requesttoken,String pin) throws TwitterException
    {
        AccessToken accessToken = null;

        accessToken = twitter.getOAuthAccessToken(requesttoken, pin);

        return accessToken;
    }

    public void storeAccessToken(AccessToken accessToken)
    {
        //ファイル名の生成
        File f = createAccessTokenFileName();

        //親ディレクトリが存在しない場合，親ディレクトリを作る．
        File d = f.getParentFile();
        if (!d.exists())
        {
            d.mkdirs();
        }

        //ファイルへの書き込み
        ObjectOutputStream os = null;
        try
        {
            os = new ObjectOutputStream(new FileOutputStream(f));
            os.writeObject(accessToken);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public File createAccessTokenFileName()
    {
        String s = eewAlert.getDataFolder() + "/AccessToken.yml";
        return new File(s);
    }
}