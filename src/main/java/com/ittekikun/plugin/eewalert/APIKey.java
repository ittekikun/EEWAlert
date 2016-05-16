package com.ittekikun.plugin.eewalert;

import java.io.Serializable;

//難読化(笑)の為可読性皆無
public class APIKey implements Serializable
{
    private static final long serialVersionUID = 1145148101919L;

    private String love;
    private String live;
    private String idol;
    private String master;


    public APIKey(String love, String live, String idol, String master)
    {
        this.love = love;
        this.live = live;
        this.idol = idol;
        this.master = master;
    }

    public String getLove()
    {
        return love;
    }

    public String getLive()
    {
        return live;
    }

    public String getIdol()
    {
        return idol;
    }

    public String getMaster()
    {
        return master;
    }
}