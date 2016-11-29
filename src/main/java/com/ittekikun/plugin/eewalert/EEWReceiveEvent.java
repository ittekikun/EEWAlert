package com.ittekikun.plugin.eewalert;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EEWReceiveEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    public EEW eew;

    public EEWReceiveEvent(EEW eew)
    {
        this.eew = eew;
    }

    /**
     * getEewArray
     *
     * eewbotのツイートを「,」で区切って配列に代入した物を返します。
     * 使うならこちらを使うべきでしょう。
     *
     * フォーマットについては下記を御覧ください。
     * http://d.hatena.ne.jp/Magi/20110403
     *
     * 非推奨理由はメソッド名をまだ決定しかねてるからです。
     */
    @Deprecated
    public String[] getEewArray()
    {
        return eew.getEewArray();
    }

    /**
     * getEew
     *
     * eewbotのツイートを元に作ったEEWクラスを返します。
     * まだ未実装の部分や一部型を変更する可能性があるので使用はおすすめしかねます。
     *
     * どなたか良いアドバイス下さい・・・
     *
     * 非推奨理由は上記のとおりです。
     */
    @Deprecated
    public EEW getEew()
    {
        return eew;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}