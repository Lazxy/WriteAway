package com.work.lazxy.writeaway.utils;

import com.work.lazxy.writeaway.common.Constant;

import java.text.DecimalFormat;

/**
 * Created by Lazxy on 2017/2/7.
 */

public class StringUtils {
    public static String transPercent(float percent){
        DecimalFormat format=new DecimalFormat("0.00");
        return format.format(percent);
    }
    public static String formatDate(int date){
        return date/10000+"."+(date/100-date/10000*100)+"."+(date-date/100*100);
    }
    public static int reformatDate(String date){
        String[] items=date.split("\\.");
        return Integer.decode(items[0])*10000+ Integer.decode(items[1])*100+ Integer.decode(items[2]);
    }

    public static String getPreview(String content){
        if(content.length()> Constant.Common.PREVIEW_MAX_LENGTH)
            return content.substring(0, Constant.Common.PREVIEW_MAX_LENGTH);
        else
            return content;
    }
}
