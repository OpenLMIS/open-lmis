/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Wolde
 * Date: 8/12/13
 * Time: 4:49 AM
 */
public class InteractiveReportPeriodFilterParser {
    
    public static final String START_DATE = "startDate";

    public static final String END_DATE = "endDate";
    
    public static Map<String,Date> getDateFilterValues(Map<String, String[]> filterCriteria){
         String periodType;
         int yearFrom;
         int yearTo;
         int monthFrom;
         int monthTo;
         Date startDate;
         Date endDate;
         int quarterFrom;
         int quarterTo;
         int semiAnnualFrom;
         int semiAnnualTo;

        Calendar originalStart = Calendar.getInstance();
        Calendar originalEnd = Calendar.getInstance();

        yearFrom = filterCriteria.get("fromYear") == null ? originalStart.get(Calendar.YEAR) : Integer.parseInt(filterCriteria.get("fromYear")[0]); //defaults to 0
        yearTo = filterCriteria.get("toYear") == null ? originalEnd.get(Calendar.YEAR) : Integer.parseInt(filterCriteria.get("toYear")[0]); //defaults to 0
        monthFrom = filterCriteria.get("fromMonth") == null ? originalStart.get(Calendar.MONTH) : Integer.parseInt(filterCriteria.get("fromMonth")[0]); //defaults to 0
        monthTo = filterCriteria.get("toMonth") == null ? originalEnd.get(Calendar.MONTH) : Integer.parseInt(filterCriteria.get("toMonth")[0]); //defaults to 0
        periodType = filterCriteria.get("periodType") == null ? "" : filterCriteria.get("periodType")[0].toString();
        quarterFrom = filterCriteria.get("fromQuarter") == null ? 1 : Integer.parseInt(filterCriteria.get("fromQuarter")[0]);
        quarterTo = filterCriteria.get("toQuarter") == null ? 1 : Integer.parseInt(filterCriteria.get("toQuarter")[0]);
        semiAnnualFrom = filterCriteria.get("fromSemiAnnual") == null ? 1 : Integer.parseInt(filterCriteria.get("fromSemiAnnual")[0]);
        semiAnnualTo = filterCriteria.get("toSemiAnnual") == null ? 1 : Integer.parseInt(filterCriteria.get("toSemiAnnual")[0]);

        int mFrom = 0;
        int mTo = 0;

        if(periodType.equals(Constants.PERIOD_TYPE_QUARTERLY)){
            mFrom = 3 *(quarterFrom - 1);
            mTo =  3 * quarterTo - 1;

        }else if(periodType.equals(Constants.PERIOD_TYPE_MONTHLY)){
            mFrom = monthFrom;
            mTo = monthTo;

        }else if(periodType.equals(Constants.PERIOD_TYPE_SEMI_ANNUAL)){
            mFrom = 6 * (semiAnnualFrom - 1);
            mTo = 6 * semiAnnualTo - 1;
        }else if(periodType.equals(Constants.PERIOD_TYPE_ANNUAL)){
            mFrom = 0;
            mTo = 11;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearFrom);
        calendar.set(Calendar.MONTH, mFrom);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        startDate = calendar.getTime();

        calendar.set(Calendar.YEAR, yearTo);
        calendar.set(Calendar.MONTH, mTo);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDate = calendar.getTime();

        Map<String ,Date> filteringPeriods = new HashMap<>(2);
        filteringPeriods.put(START_DATE,startDate);
        filteringPeriods.put(END_DATE,endDate);

        return filteringPeriods;
    }

    public static Date getStartDateFilterValue(Map<String, String[]> filterCriteria){

        Map<String ,Date> filteringPeriods = InteractiveReportPeriodFilterParser.getDateFilterValues(filterCriteria);

        if(filteringPeriods == null) return  null;

        return filteringPeriods.get(START_DATE);
    }

    public static Date getEndDateFilterValue(Map<String, String[]> filterCriteria){

        Map<String ,Date> filteringPeriods = InteractiveReportPeriodFilterParser.getDateFilterValues(filterCriteria);

        if(filteringPeriods == null) return  null;

        return filteringPeriods.get(END_DATE);
    }
}
