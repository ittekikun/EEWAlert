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

    public String[] getEEW()
    {
        return eew.getEewArray();
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
