package org.openlmis.report.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	public static final String EMPTY_STRING = "";

	public static String formatDate(Date date) {
		if (date == null) {
			return EMPTY_STRING;
		}
		return new SimpleDateFormat(DateUtil.FORMAT_DATE_TIME).format(date);
	}
}
