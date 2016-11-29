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

    public boolean isRetweet;

    public String maxScale;
    public String epicenter;
    public String occurrenceTime;
    public String depth;
    public String magnitude;

    public IdentificationType identificationType;
    public FocusType focusType;
    public AlarmType alarmType;

    public EEW(String[] array, boolean isRetweet)
    {
        //生データ代入
        this.eewArray = array;

        //RTされた物か代入
        this.isRetweet = isRetweet;

        //電文の種類(未実装 0

        //訓練識別符代入 1
        if((Integer.parseInt(array[1])) == 00)
        {
            identificationType = NORMAL;
        }
        else if((Integer.parseInt(array[1])) == 01)
        {
            identificationType = DRILL;
        }

        //発表時刻（未実装 2

        //発表状況（未実装 3

        //電文番号（未実装 4

        //地震ID（未実装 5

        //地震発生時刻 6
        this.occurrenceTime = array[2];

        //震源の北緯（未実装 7

        //震源の東経（未実装 8

        //震央地名 9
        this.epicenter = array[9];

        //震源の深さ 10
        this.depth = array[10];

        //マグニチュード 11
        this.magnitude = array[11];


        //最大震度 12
        this.maxScale = array[12];
//        if (array[12].equals("0"))
//        {
//            this.maxScale = Scale._0_;
//        }
//        else if(array[12].equals("1"))
//        {
//            this.maxScale = Scale._1_;
//        }
//        else if(array[12].equals("2"))
//        {
//            this.maxScale = Scale._2_;
//        }
//        else if(array[12].equals("3"))
//        {
//            this.maxScale = Scale._3_;
//        }
//        else if(array[12].equals("4"))
//        {
//            this.maxScale = Scale._4_;
//        }
//        else if(array[12].equals("5弱"))
//        {
//            this.maxScale = Scale._5zyaku_;
//        }
//        else if(array[12].equals("5強"))
//        {
//            this.maxScale = Scale._5kyou_;
//        }
//        else if(array[12].equals("6弱"))
//        {
//            this.maxScale = Scale._6zyaku_;
//        }
//        else if(array[12].equals("6強"))
//        {
//            this.maxScale = Scale._6kyou_;
//        }
//        else if(array[12].equals("7"))
//        {
//            this.maxScale = Scale._7_;
//        }
//        else if(array[12].equals("不明"))
//        {
//            this.maxScale = Scale.unknown;
//        }

        //震源の海陸判定 13
        if((Integer.parseInt(array[13])) == 0)
        {
            focusType = LAND;
        }
        else if((Integer.parseInt(array[13])) == 1)
        {
            focusType = SEA;
        }

        //警報タイプ 14
        if((Integer.parseInt(array[14])) == 1)
        {
            alarmType = GENERAL;
        }
        else if((Integer.parseInt(array[14])) == 0)
        {
            alarmType = ADVANCED;
        }
    }

    //CSVを区切って配列に代入した生データ
    public String[] getEewArray()
    {
        return eewArray;
    }

    //RTされた物であるか
    public boolean isRetweet()
    {
        return isRetweet;
    }

    //電文の種類（未実装 0

    //訓練識別符 1
    public IdentificationType getIdentificationType()
    {
        return identificationType;
    }

    //発表時刻（未実装 2

    //発表状況（未実装 3

    //電文番号（未実装 4

    //地震ID（未実装 5

    //地震発生時刻 6
    public String getOccurrenceTime()
    {
        return occurrenceTime;
    }

    //震源の北緯（未実装 7

    //震源の東経（未実装 8

    //震央地名 9
    public String getEpicenter()
    {
        return epicenter;
    }

    //震源の深さ 10
    public String getDepth()
    {
        return depth;
    }

    //マグニチュード 11
    public String getMagnitude()
    {
        return magnitude;
    }

    //最大震度 12
    public String getMaxScale()
    {
        return maxScale;
    }

    //震源の海陸判定 12
    public FocusType getFocusType()
    {
        return focusType;
    }

    //警報の有無 14
    public AlarmType getAlarmType()
    {
        return alarmType;
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

    public enum Scale
    {
        unknown,
        _0_,
        _1_,
        _2_,
        _3_,
        _4_,
        _5zyaku_,
        _5kyou_,
        _6zyaku_,
        _6kyou_,
        _7_,
    }
}