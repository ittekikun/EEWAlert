package com.ittekikun.plugin.eewalert;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

import static com.ittekikun.plugin.eewalert.EEW.AlarmType.ADVANCED;
import static com.ittekikun.plugin.eewalert.EEW.AlarmType.GENERAL;

public class NoticeBuilder
{
    public static void noticeEewMessage(EEW eew)
    {
        List<String> eewMes = new ArrayList<String>();

        if(eew.alarmType == GENERAL)
        {
            if((Integer.parseInt(eew.eewArray[3]) == 0) || (Integer.parseInt(eew.eewArray[3]) == 8) || (Integer.parseInt(eew.eewArray[3]) == 9))
            {
                eewMes.add(ChatColor.RED +    "----------緊急地震速報----------");

                eewMes.add(ChatColor.YELLOW + "発表時刻: " + ChatColor.WHITE + eew.getOccurrenceTime());
                eewMes.add(ChatColor.YELLOW + "震源地: " + ChatColor.WHITE + eew.getEpicenter());
                eewMes.add(ChatColor.YELLOW + "マグニチュード: " + ChatColor.WHITE + eew.getMagnitude());
                eewMes.add(ChatColor.YELLOW + "深さ: " + ChatColor.WHITE + eew.getDepth());
                eewMes.add(ChatColor.YELLOW + "最大震度: " + ChatColor.WHITE + eew.getMaxScale());
                eewMes.add(ChatColor.RED +    "震源地付近にお住まいの方は大きな地震に注意してください。");
                eewMes.add(ChatColor.RED +    "この情報を鵜呑みにせず、テレビ・ラジオ等で正確な情報を収集してください。");
                eewMes.add(ChatColor.RED +    "※この情報は震度速報ではありません。あくまでも、地震の規模を早期に推定するものです。");
                eewMes.add(ChatColor.RED +    "--------------------------------");
            }
        }
        else if(eew.alarmType == ADVANCED)
        {
            eewMes.add(ChatColor.RED +    "----------緊急地震速報(動作確認モード有効中)----------");
            eewMes.add(ChatColor.RED +    "テレビなどで普段発表されていない緊急地震速報を表示しています。");
            eewMes.add(ChatColor.RED +    "念の為、テレビ・ラジオ等で正確な情報を収集してください。");
            eewMes.add(ChatColor.RED +    "動作確認出来し次第、動作確認モードを無効にして下さい。");

            eewMes.add(ChatColor.YELLOW + "発表時刻: " + ChatColor.WHITE + eew.getOccurrenceTime());
            eewMes.add(ChatColor.YELLOW + "震源地: " + ChatColor.WHITE + eew.getEpicenter());
            eewMes.add(ChatColor.YELLOW + "マグニチュード: " + ChatColor.WHITE + eew.getMagnitude());
            eewMes.add(ChatColor.YELLOW + "深さ: " + ChatColor.WHITE + eew.getDepth());
            eewMes.add(ChatColor.YELLOW + "最大震度: " + ChatColor.WHITE + eew.getMaxScale());
            eewMes.add(ChatColor.RED +    "※この情報は震度速報ではありません。あくまでも、地震の規模を早期に推定するものです。");
            eewMes.add(ChatColor.RED +    "--------------------------------");
        }
        broadcastMessage(eewMes);
    }

    public static void broadcastMessage(List eewMes)
    {

        for(int i = 0; i < eewMes.size(); ++i)
        {
            Bukkit.broadcastMessage(eewMes.get(i).toString());
        }
    }
}