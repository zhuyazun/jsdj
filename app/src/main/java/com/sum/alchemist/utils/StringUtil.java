package com.sum.alchemist.utils;

import java.util.regex.Pattern;

/**
 * Created by Qiu on 2016/11/16.
 */

public class StringUtil {

    public static String getShortDate(String date){
        if(date != null && date.length() > 10){
            return date.substring(5, 10);
        }
        return null;
    }

    public static String getDate(String time){
        if(time != null && time.length() > 10){
            return time.substring(0, 10);
        }
        return null;
    }

    public static String filter(String str, String filter){
        Pattern pattern = Pattern.compile(filter);
        return pattern.matcher(str).replaceAll("");
    }
}
