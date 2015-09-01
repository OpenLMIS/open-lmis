
DROP FUNCTION IF EXISTS fn_gettimelinessreportdata(integer, integer, integer, integer, character varying, character varying);

CREATE OR REPLACE FUNCTION fn_gettimelinessreportdata(
    IN in_programid integer,
    IN in_geographiczoneid integer,
    IN in_periodid integer,
    IN in_scheduleid integer,
    IN in_status character varying,
    IN facilityids character varying)
  RETURNS TABLE(duration date, status text, rnrid integer, facilityname text, facilitytypename text) AS
$BODY$
BEGIN

RETURN QUERY EXECUTE '
SELECT requisition_status_changes.createddate::date duration,requisition_status_changes.status::text,vw_timeliness_report.rnrId::integer,facilityname::text , facilitytypename::text 
FROM vw_timeliness_report
INNER JOIN requisition_status_changes ON vw_timeliness_report.rnrId = requisition_status_changes.rnrid

            WHERE
            requisition_status_changes.status::text <> ALL (ARRAY[''INITIATED''::character varying::text, ''SUBMITTED''::character varying::text, ''SKIPPED''::character varying::text]) AND 
 programId = ' || in_programid || ' and periodId='|| in_periodid ||' AND scheduleId = '|| in_scheduleid ||' and  reportingstatus IN ('''|| in_status ||''') and geographiczoneId = '|| in_geographiczoneid ||'
              AND facilityId IN ('|| facilityIds || ')  
                       GROUP BY requisition_status_changes.createddate,requisition_status_changes.status,vw_timeliness_report.rnrId,facilityname,facilitytypename
                       order by status';

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_gettimelinessreportdata(integer, integer, integer, integer, character varying, character varying)
  OWNER TO postgres;


  
