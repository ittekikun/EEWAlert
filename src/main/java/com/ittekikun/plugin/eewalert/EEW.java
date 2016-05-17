package com.ittekikun.plugin.eewalert;

import static com.ittekikun.plugin.eewalert.EEW.AlarmType.ADVANCED;
import static com.ittekikun.plugin.eewalert.EEW.AlarmType.GENERAL;
import static com.ittekikun.plugin.eewalert.EEW.FocusType.LAND;
import static com.ittekikun.plugin.eewalert.EEW.FocusType.SEA;
import static com.ittekikun.plugin.eewalert.EEW.IdentificationType.DRILL;
import static com.ittekikun.plugin.eewalert.EEW.IdentificationType.NORMAL;

public class EEW
{
    public String[] eewArray;

    public String maxScale;
    public String epicenter;
    public String occurrenceTime;
    public String depth;
    public String magnitude;

    public IdentificationType identificationType;
    public FocusType focusType;
    public AlarmType alarmType;

    public EEW(String[] array)
    {
        this.eewArray = array;

        occurrenceTime = array[2];
        epicenter = array[9];
        depth = array[10];
        magnitude = array[11];
        maxScale = array[12];

        if((Integer.parseInt(array[14])) == 1)
        {
            alarmType = GENERAL;
        }
        else if((Integer.parseInt(array[14])) == 0)
        {
            alarmType = ADVANCED;
        }

        if((Integer.parseInt(array[1])) == 00)
        {
            identificationType = NORMAL;
        }
        else if((Integer.parseInt(array[1])) == 01)
        {
            identificationType = DRILL;
        }

        if((Integer.parseInt(array[13])) == 0)
        {
            focusType = LAND;
        }
        else if((Integer.parseInt(array[13])) == 1)
        {
            focusType = SEA;
        }
    }

    public String getOccurrenceTime()
    {
        return occurrenceTime;
    }

    public String getEpicenter()
    {
        return epicenter;
    }

    public String getMagnitude()
    {
        return magnitude;
    }

    public String getDepth()
    {
        return depth;
    }

    public String getMaxScale()
    {
        return maxScale;
    }

    public AlarmType getAlarmType()
    {
        return alarmType;
    }

    public FocusType getFocusType()
    {
        return focusType;
    }

    public IdentificationType getIdentificationType()
    {
        return identificationType;
    }

    public String[] getEewArray()
    {
        return eewArray;
    }

    //訓練識別、通常か訓練か
    public enum IdentificationType
    {
        NORMAL,
        DRILL
    }

    //震源地の場所が陸上か海上か
     public enum FocusType
    {
        LAND,
        SEA
    }

    //警報タイプ、高度利用者向けか一般か
    public enum AlarmType
    {
        ADVANCED,
        GENERAL
    }
}