package com.cxb.myfamilytree.utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 */

public class DateTimeUtils {

    @IntDef({DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Format {
    }

    private static SimpleDateFormat enLongDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static SimpleDateFormat enDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static SimpleDateFormat enNotYearDateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
    private static SimpleDateFormat enLongTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static SimpleDateFormat enShortTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private static SimpleDateFormat cnLongDateTimeFormat = new SimpleDateFormat("yyyy年MM月dd HH时mm分ss秒", Locale.getDefault());
    private static SimpleDateFormat cnDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
    private static SimpleDateFormat cnNotYearDateFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
    private static SimpleDateFormat cnLongTimeFormat = new SimpleDateFormat("HH时mm分ss秒", Locale.getDefault());
    private static SimpleDateFormat cnShortTimeFormat = new SimpleDateFormat("HH时mm分", Locale.getDefault());
    private static SimpleDateFormat cnWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

    public static final int DAY = 0;
    public static final int MONTH = 1;
    public static final int YEAR = 2;

    // 获取当前日期，格式根据datetype决定
    //DateFormat.FULL, 2016年12月1日 星期四
    //DateFormat.LONG 2016年12月1日
    //DateFormat.MEDIUM 2016年12月1日
    //DateFormat.SHORT 16/12/1
    public static String getCurrentDate(@Format int dateType) {
        return SimpleDateFormat.getDateInstance(dateType).format(new Date());
    }

    // 获取当前时间，格式根据timeType决定
    //DateFormat.FULL 中国标准时间16:07:13
    //DateFormat.LONG GMT+08:0016:07:13
    //DateFormat.MEDIUM 16:07:13
    //DateFormat.SHORT 16:07
    public static String getCurrentTime(@Format int timeType) {
        return SimpleDateFormat.getTimeInstance(timeType).format(new Date());
    }

    // 获取当前日期时间，格式根据datetype 和 timeType 决定
    public static String getCurrentDateTime(@Format int dateType, @Format int timeType) {
        return SimpleDateFormat.getDateTimeInstance(dateType, timeType).format(new Date());
    }


    // 获取当前日期时间，格式：2016-11-24 23:33:33
    public static String getEnLongDateTime() {
        return enLongDateTimeFormat.format(new Date());
    }

    // 获取当前日期，格式：2016-11-24
    public static String getEnDate() {
        return enDateFormat.format(new Date());
    }

    // 获取当前日期，格式：11-24
    public static String getEnNotYearDate() {
        return enNotYearDateFormat.format(new Date());
    }

    // 获取当前时间，格式：23:33:33
    public static String getEnLongTime() {
        return enLongTimeFormat.format(new Date());
    }

    // 获取当前时间，格式：23:33
    public static String getEnShortTime() {
        return enShortTimeFormat.format(new Date());
    }

    // 获取当前日期时间，格式：2016年11月24日 23时33分33秒
    public static String getCnLongDateTime() {
        return cnLongDateTimeFormat.format(new Date());
    }

    // 获取当前日期，格式：2016年11月24日
    public static String getCnDate() {
        return cnDateFormat.format(new Date());
    }

    // 获取当前日期，格式：11月24日
    public static String getCnNotYearDate() {
        return cnNotYearDateFormat.format(new Date());
    }

    // 获取当前时间，格式：23时33分33秒
    public static String getCnLongTime() {
        return cnLongTimeFormat.format(new Date());
    }

    // 获取当前时间，格式：23时33分
    public static String getCnShortTime() {
        return cnShortTimeFormat.format(new Date());
    }

    // 获取当前星期，格式：星期四
    public static String getCnWeek() {
        return cnWeekFormat.format(new Date());
    }

    // 根据范围类型获取友好日期时间
    public static String getFriendlyDateTime(int range, String datetime) {
        if (range == DAY) {
            return getFriendlyDay(datetime);
        } else if (range == MONTH) {
            return getFriendlyMonth(datetime);
        } else if (range == YEAR) {
            return getFriendlyYear(datetime);
        } else {
            return datetime;
        }
    }

    // 日期字符串转成友好月份（xx年后，明年，今年，去年，xx年前）
    public static String getFriendlyYear(String dateTimeStr) {
        int year = getNumberOfYear(new Date(), StringToDate(dateTimeStr));

        if (year < -1) {
            return Math.abs(year) + "年后";
        } else if (year == -1) {
            return "明年";
        } else if (year == 0) {
            return getFriendlyMonth(dateTimeStr);
        } else if (year == 1) {
            return "去年";
        } else if (year > 1) {
            return Math.abs(year) + "年前";
        }

        return dateTimeStr;
    }

    // 日期字符串转成友好月份（xx个月后，次月，当月，上个月，xx个月前）
    public static String getFriendlyMonth(String dateTimeStr) {
        int month = getNumberOfMonth(new Date(), StringToDate(dateTimeStr));
        if (month < -1) {
            return Math.abs(month) + "个月后";
        } else if (month == -1) {
            return "次月";
        } else if (month == 0) {
            return getFriendlyDay(dateTimeStr);
        } else if (month == 1) {
            return "上个月";
        } else if (month > 1) {
            return Math.abs(month) + "个月前";
        }

        return dateTimeStr;
    }

    // 日期字符串转换成友好日期（xx天后，明天，今天，昨天，xx天前）
    public static String getFriendlyDay(String dateTimeStr) {

        int days = getNumberOfDays(new Date(), StringToDate(dateTimeStr));
        if (days < -1) {
            return Math.abs(days) + "天后";
        } else if (days == -1) {
            return "明天";
        } else if (days == 0) {
            return getFriendlyHourAndMinute(dateTimeStr);
        } else if (days == 1) {
            return "昨天";
        } else if (days > 1) {
            return Math.abs(days) + "天前";
        }

        return dateTimeStr;
    }

    // 日期字符串转换成友好时间（xx小时后，xx分钟后，刚刚，xx分钟前，xx小时前）
    public static String getFriendlyHourAndMinute(String dateTimeStr) {
        int minute = getNumberOfMinute(new Date(), StringToDate(dateTimeStr));
        int absMinute = Math.abs(minute);
        String suffix = minute > 0 ? "前" : "后";

        if (absMinute < 60) {
            if (absMinute == 0) {
                return "刚刚";
            } else {
                return absMinute + "分钟" + suffix;
            }
        } else {
            int hour = absMinute / 60;
            return hour + "小时" + suffix;
        }
    }

    //获取两个日期的年份差（忽略时分秒）
    public static int getNumberOfYear(Date date1, Date date2) {
        return getNumberOfMonth(date1, date2) / 12;
    }

    //获取两个日期的月数差（忽略时分秒）
    public static int getNumberOfMonth(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalAccessError("string can not convert date");
        } else {
            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();

            calendar1.setTime(date1);
            calendar2.setTime(date2);

            int year1 = calendar1.get(Calendar.YEAR);
            int year2 = calendar2.get(Calendar.YEAR);

            int month1 = calendar1.get(Calendar.MONTH) + 1;
            int month2 = calendar2.get(Calendar.MONTH) + 1;

            if (year1 - year2 == 0) {
                return month1 - month2;
            } else {
                return (month1 - month2) + (year1 - year2) * 12;
            }
        }

    }

    // 获取两个日期的天数差（忽略时分秒）
    public static int getNumberOfDays(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalAccessError("string can not convert date");
        } else {
            int dateStamp1 = (int) (date1.getTime() / (24 * 60 * 60 * 1000));
            int dateStamp2 = (int) (date2.getTime() / (24 * 60 * 60 * 1000));

            return dateStamp1 - dateStamp2;
        }
    }

    // 获取两个时间的分钟差
    public static int getNumberOfMinute(Date time1, Date time2) {
        if (time1 == null || time2 == null) {
            throw new IllegalAccessError("string can not convert date");
        } else {
            int timeStamp1 = (int) (time1.getTime() / (60 * 1000));
            int timeStamp2 = (int) (time2.getTime() / (60 * 1000));

            return timeStamp1 - timeStamp2;
        }
    }

    // 日期字符串转日期对象
    public static Date StringToDate(String dateTimeStr) {
        try {
            return enLongDateTimeFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 日期字符串转日期对象（忽略时分秒）
    public static Date StringToDateIgnoreTime(String dateTimeStr) {
        try {
            return enDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 日期字符串转时间对象（忽然年月日）
    public static Date StringToTimeIgnoreDate(String dateTimeStr) {
        try {
            return enLongTimeFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 判断是否 2016-11-24日期格式
    public static boolean isDate(String dateStr) {
        try {
            return enDateFormat.parse(dateStr) != null;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 判断是否 23:33:33格式
    public static boolean isTime(String timeStr) {
        try {
            return enLongTimeFormat.parse(timeStr) != null;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

}
