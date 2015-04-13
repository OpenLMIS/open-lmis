DROP FUNCTION IF EXISTS fn_get_timeliness_reporting_dates(integer);

CREATE OR REPLACE FUNCTION fn_get_timeliness_reporting_dates(IN in_periodid integer)
  RETURNS TABLE(reportingstartdate date, reportingenddate date, reportinglatestartdate date, reportinglateenddate date) AS
$BODY$
BEGIN

RETURN QUERY EXECUTE 
'SELECT CAST(date_trunc(''month'', enddate::date) + INTERVAL ''1 month'' as date) reportingStartDate, 

 (enddate::date + COALESCE((( SELECT configuration_settings.value FROM configuration_settings
 
 WHERE configuration_settings.key::text = ''MSD_ZONE_REPORTING_CUT_OFF_DATE''::text))::integer, 0)::integer ) reportingEndDate,
 
(CAST(date_trunc(''month'', enddate::date) + INTERVAL ''1 month'' as date) + COALESCE((( SELECT configuration_settings.value FROM configuration_settings

 WHERE configuration_settings.key::text = ''MSD_ZONE_REPORTING_CUT_OFF_DATE''::text))::integer, 0)::integer ) lateReportingStartDate,
 
 (enddate::date + COALESCE((( SELECT configuration_settings.value FROM configuration_settings
 
 WHERE configuration_settings.key::text = ''UNSCHEDULED_REPORTING_CUT_OFF_DATE''::text))::integer, 0)::integer ) lateReportingEndDate FROM processing_periods
  
 WHERE   id = ' || in_periodid || '';
 
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_get_timeliness_reporting_dates(integer)
  OWNER TO postgres;
