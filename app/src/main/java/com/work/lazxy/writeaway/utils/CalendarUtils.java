package com.work.lazxy.writeaway.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lenovo on 2017/1/31.
 */

public class CalendarUtils {
    public static final long SECOND_OF_MILLI = 1000;
    public static final long MINUTE_OF_MILLI = 60 * SECOND_OF_MILLI;
    public static final long HOUR_OF_MILLI = 60 * MINUTE_OF_MILLI;
    public static final long DAY_OF_MILLI = 24 * HOUR_OF_MILLI;
    public static final long WEEK_OF_MILLI = DAY_OF_MILLI * 7;
    public static final long MONTH_OF_MILLI = DAY_OF_MILLI * 30;
    public static final long YEAR_OF_MILLI = DAY_OF_MILLI * 365;
    public static final String END_TIME = "2333-2-3 23:33";
    public static final String CUSTOM_FORMAT = "yyyy-MM-dd HH:mm";

    public static String getFormatTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(CUSTOM_FORMAT, Locale.CHINA);
        return sdf.format(date);
    }
    /**
     * 根据时间戳获取描述性时间，如3分钟前，1天前
     *
     * @param timestamp 时间戳 单位为秒
     * @return 时间字符串
     */
    public static String getTimeFromTimestamp(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeGap = (currentTime - timestamp);

        String time;
        if (timeGap > YEAR_OF_MILLI) {
            time = timeGap / YEAR_OF_MILLI + "年前";
        } else if (timeGap > MONTH_OF_MILLI) {
            time = timeGap / MONTH_OF_MILLI + "个月前";
        } else if (timeGap > DAY_OF_MILLI) {
            time = timeGap / DAY_OF_MILLI + "天前";
        } else if (timeGap > HOUR_OF_MILLI) {
            time = timeGap / HOUR_OF_MILLI + "小时前";
        } else if (timeGap > MINUTE_OF_MILLI) {
            time = timeGap / MINUTE_OF_MILLI + "分钟前";
        } else {
            time = "刚刚";
        }
        return time;
    }

    /**
     * 用ms、s、m-s或者h-m-s的形式转化毫秒值
     *
     * @param millis 待转化的毫秒值
     * @return 一个表示时间的字符串
     */
    public static String transMillisSuitable(long millis) {
        if (millis < SECOND_OF_MILLI) {
            return millis + "ms";
        } else if (millis < MINUTE_OF_MILLI) {
            return millis / SECOND_OF_MILLI + "s";
        } else if (millis < HOUR_OF_MILLI) {
            long minute = millis / (MINUTE_OF_MILLI);
            return minute + "m" + (millis / SECOND_OF_MILLI - minute * 60) + "s";
        } else {
            long hour = millis / HOUR_OF_MILLI;
            long minute = millis / MINUTE_OF_MILLI - hour * 60;
            return hour + "h" + minute + "m" + (millis / SECOND_OF_MILLI - minute * 60 - hour * 60 * 60) + "s";
        }
    }

    public static int transMillisToDate(long millis) {
        Date date = new Date(millis);
        return (date.getYear() + 1900) * 10000 + (date.getMonth() + 1) * 100 + date.getDate();
    }

    public static int getToday() {
        Date date = Calendar.getInstance().getTime();
        return (date.getYear() + 1900) * 10000 + (date.getMonth() + 1) * 100 + date.getDate();
    }

    public static String getTodayInCustomFormat() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat(CUSTOM_FORMAT);
        return format.format(date);
    }

    public static long reformatCustomDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(CUSTOM_FORMAT);
        long millionSeconds = -1;
        try {
            millionSeconds = sdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millionSeconds;
//        return targetTimeFromNow(date,CUSTOM_FORMAT);
    }

    /**
     * 将时间差转化为一定格式的时间数。
     *
     * @param dif
     * @return 返回一个时间值，格式为XX年XXX天XX时XX分XX秒。
     */
    public static String getTimeDifference(long dif) {
        int year = (int) (dif / YEAR_OF_MILLI);
        int day = (int) ((dif - year * YEAR_OF_MILLI) / DAY_OF_MILLI);
        int hour = (int) ((dif - year * YEAR_OF_MILLI - day * DAY_OF_MILLI) / HOUR_OF_MILLI);
        int minute = (int) ((dif - year * YEAR_OF_MILLI - day * DAY_OF_MILLI - hour * HOUR_OF_MILLI) / MINUTE_OF_MILLI);
        int second = (int) ((dif - year * YEAR_OF_MILLI - day * DAY_OF_MILLI - hour * HOUR_OF_MILLI - minute * MINUTE_OF_MILLI) / SECOND_OF_MILLI);
//        return (long)(year*Math.pow(10,9)+day*Math.pow(10,6)+hour*Math.pow(10,4)+minute*Math.pow(10,2)+second);
        return arrangeTimeInInt(new int[]{year, day, hour, minute, second});
    }

    public static String arrangeTimeInInt(int[] dates) {
        StringBuffer buffer = new StringBuffer();
        if (dates[0] != 0)
            buffer.append(dates[0] + "y");
        if (buffer.length() > 0 || dates[1] != 0)
            buffer.append(dates[1] + "d");
        if (buffer.length() > 0 || dates[2] != 0)
            buffer.append(dates[2] + "h");
        buffer = dates[3] < 10 ? buffer.append("0" + dates[3] + "m") : buffer.append(dates[3] + "m");
        buffer = dates[4] < 10 ? buffer.append("0" + dates[4] + "s") : buffer.append(dates[4] + "s");
        return buffer.toString();
    }
}
