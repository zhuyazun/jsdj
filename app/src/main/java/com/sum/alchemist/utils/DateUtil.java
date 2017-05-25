package com.sum.alchemist.utils;

import com.sum.xlog.core.XLog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间工具类
 * Created by Qiu on 2016/5/5
 */
public class DateUtil {

    private final static String TAG = "DateUtil";

    /**
     * 根据传入时间累加N天后的0点时间
     * @param addDay 累加天数
     * @return 最终时间
     */
    public static Date addDateByDay(int addDay, long millis){
        long timeMillis = millis + 86400000L * addDay;
        timeMillis = (timeMillis / 86400000) * 86400000;
        return new Date(timeMillis);
    }

    /**
     * 根据时间字符串及时间格式返回时间毫秒数
     * @param dateString 时间字符串
     * @param timeType 时间格式
     */
    public static long string2Millis(String dateString, String timeType, boolean isTimeZone){
        long result = 0;
        DateFormat dateFormat = new SimpleDateFormat(timeType, Locale.US);
        if(isTimeZone)
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date date = dateFormat.parse(dateString);
            result = date.getTime();
        } catch (Exception e) {
            XLog.e(TAG, "解析时间失败 dateString:%s, timeType:%s", e, dateString, timeType);
        }
        return result;
    }

    public static String millis2String(long millis, String timeType, boolean isTimeZone){
        String result = "";
        DateFormat dateFormat = new SimpleDateFormat(timeType, Locale.US);
        if(isTimeZone)
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            result = dateFormat.format(new Date(millis));
        } catch (Exception e) {
            XLog.e(TAG, "解析时间失败 millis:%s, timeType:%s", e, millis, timeType);
        }
        return result;
    }

    public static long YMD2long(int year, int month, int day){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(year, month, day);
        return calendar.getTimeInMillis() / 1000;
    }

    public static int getCurrentYear(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.YEAR);
    }

    public static int getCurrentMonth(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getCurrentDay(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }



}
