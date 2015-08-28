package org.openlmis.core.utils;

import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;


public class DateUtil
{
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
}
