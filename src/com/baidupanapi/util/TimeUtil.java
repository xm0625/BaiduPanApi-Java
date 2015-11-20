package com.baidupanapi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xm on 15-11-20.
 */
public class TimeUtil {

    public static String getSecondTime(){
        return String.valueOf(System.currentTimeMillis()).substring(0,10);
    }

}
