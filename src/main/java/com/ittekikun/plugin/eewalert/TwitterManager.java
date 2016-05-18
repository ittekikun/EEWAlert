package com.ittekikun.plugin.eewalert;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class TwitterManager
{
    public EEWAlert eewAlert;
    public Twitter twitter;
    public TwitterStream eewStream;
    public RequestToken requestToken;
    public APIKey apiKey;

    public boolean canTweet = false;
    public boolean canAuth = false;

    public TwitterManager(EEWAlert eewAlert)
    {
        this.eewAlert = eewAlert;
        this.apiKey = eewAlert.apiKey;
    }

    public void startSetup()
    {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.setOAuthConsumerKey(apiKey.getIdol());
        builder.setOAuthConsumerSecret(apiKey.getMaster());
        Configuration conf = builder.build();

        AccessToken accessToken = null;

        twitter = new TwitterFactory(conf).getInstance();
        eewStream = new TwitterStreamFactory(conf).getInstance();

        accessToken = loadAccessToken();

        //初期起動時(ファイルなし)
        if(accessToken == null)
        {
            startSetupGuide();
        }
        //ファイル有り
        else
        {
            twitter.setOAuthAccessToken(accessToken);
            eewStream.setOAuthAccessToken(accessToken);

            startRecieveStream();

            canTweet = true;
        }
    }

    public URL createOAuthUrl()
    {
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
            if(is != null)
            {
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

    public AccessToken getAccessToken(String pin) throws TwitterException
    {
        AccessToken accessToken = null;

        accessToken = twitter.getOAuthAccessToken(requestToken, pin);

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
        String s = eewAlert.getDataFolder() + "/AccessToken.dat";
        return new File(s);
    }

    void startSetupGuide()
    {
        java.util.List<String> firstMes = new ArrayList<String>();

        firstMes.add("#################################################");
        firstMes.add("[[[[ Twitter連携ウィザード EEWAlert by ittekikun ]]]]");
        firstMes.add("EEWAlertのTwitter連携設定がされてません。");
        firstMes.add("下記URLから認証後、PINコードを /eew pin <pin> の様に打ち込み連携を完了して下さい。");
        //firstMes.add("※");
        try
        {
            firstMes.add("URL: " + Utility.getShortUrl(createOAuthUrl().toString(), apiKey.getLove()));
            //firstMes.add("URL: " + (createOAuthUrl().toString()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            eewAlert.log.severe("認証URLの生成に失敗しました。");
            return;
        }
        firstMes.add("#################################################");

        infoList(firstMes);
        canAuth = true;
    }

    void infoList(java.util.List<String> list)
    {
        for(int i = 0; i < list.size(); ++i)
        {
            eewAlert.log.info(list.get(i).toString());
        }
    }

    public void startRecieveStream()
    {
        eewStream.addListener(new EEWStream(eewAlert));
        eewStream.user();

        EEWAlert.log.info("ユーザーストリームに接続しました。");

        //214358709 = @eewbot
        long[] list = {214358709L};
        FilterQuery query = new FilterQuery(list);
        eewStream.filter(query);
    }

    public void shutdownRecieveStream()
    {
        eewStream.shutdown();
        //TODO koko
        //EEWAlert.log.info("ユーザーストリームから切断しました。。");
    }
}