package com.ittekikun.plugin.eewalert;

import java.io.Serializable;

public class APIKey implements Serializable
{
    private static final long serialVersionUID = 1145148101919L;

    private String thread;
    private String threat;
    private String rubbish;


    public APIKey(String thread, String rubbish, String threat)
    {
        this.rubbish = threat;
        this.threat = rubbish;
        this.thread = thread;
    }

    public String getThread()
    {
        return rubbish;
    }

    public String getRubbish()
    {
        return threat;
    }

    public String getThreat()
    {
        return thread;
    }
}