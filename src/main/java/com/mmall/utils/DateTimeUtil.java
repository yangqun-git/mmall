package com.mmall.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by yangqun on 2017/12/25.
 */
public class DateTimeUtil {

    //date --> str
    //str --> date
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String dateTimeStr,String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }
    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }
    public static String dateToStr(Date dateTimeStr,String formatStr){
        if (dateTimeStr == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(dateTimeStr);
        return dateTime.toString(formatStr);
    }
    public static String dateToStr(Date dateTimeStr){
        if (dateTimeStr == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(dateTimeStr);
        return dateTime.toString(STANDARD_FORMAT);
    }

}
