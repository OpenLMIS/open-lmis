DROP FUNCTION IF EXISTS fn_get_reporting_status_by_facilityid_programid_and_periodid(integer,integer,integer);

CREATE OR REPLACE FUNCTION fn_get_reporting_status_by_facilityid_programid_and_periodid(v_facilityid integer,v_programid integer,v_periodid integer)
  RETURNS TEXT AS
$BODY$
DECLARE
v_ret TEXT;
v_reporting_date INTEGER;
v_late_days INTEGER;
v_req_facilityid INTEGER;
BEGIN

select facilityid from requisitions where facilityid = v_facilityid and programid = v_programid and periodid = v_periodid INTO v_req_facilityid;

IF v_req_facilityid IS NULL THEN RETURN 'non_reporting'; END IF;

SELECT value from configuration_settings where key='LATE_REPORTING_DAYS' INTO v_late_days;

SELECT date_part('day', (select createddate from requisitions r where r.programId = v_programid and r.periodId = v_periodid and facilityid = v_facilityid)- 
(select startdate from processing_periods where id = v_periodid))::integer INTO v_reporting_date;

SELECT CASE WHEN 
COALESCE(v_reporting_date,0) > COALESCE(v_late_days,10)
  THEN 'late_reporting' 
  ELSE 'reporting' END INTO v_ret;

return v_ret;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_get_reporting_status_by_facilityid_programid_and_periodid(integer,integer,integer)
  OWNER TO postgres;