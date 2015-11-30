package org.openlmis.core.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil
{
    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String EMPTY_STRING = "";
    public static final String FORMAT_DATE_TIME_DAY_MONTH_YEAR = "dd/MM/yyyy";

    public static String getFormattedDate(Date date, String format)
    {
        if(date == null)
            return null;

        try {
            return  DateFormatUtils.format(date, format);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return EMPTY_STRING;
        }
        return new SimpleDateFormat(DateUtil.FORMAT_DATE_TIME).format(date);
    }

    public static Date parseDate(String date) {
        return parseDate(date, FORMAT_DATE_TIME);
    }

    public static Date parseDate(String date, String pattern) {
        if (date == null) {
            return null;
        }

        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
