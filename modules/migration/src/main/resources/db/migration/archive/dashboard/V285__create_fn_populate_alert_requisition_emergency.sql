
-- Function: fn_populate_alert_requisition_emergency()

 DROP FUNCTION IF EXISTS fn_populate_alert_requisition_emergency();

CREATE OR REPLACE FUNCTION fn_populate_alert_requisition_emergency()
  RETURNS character varying AS
$BODY$
DECLARE
rec_summary RECORD ;
rec_detail RECORD ;

msg CHARACTER VARYING (2000) ;
v_summaryid integer;

BEGIN
msg := 'fn_populate_alert_requisition_emergency - Data saved successfully' ;
delete from alert_summary where alerttypeid = 'EMERGENCY_REQUISITION';

FOR rec_summary IN
SELECT
geographiczoneid, programid, periodid, count(rnrid) rec_count
FROM
vw_facility_requisitions
where emergency = true
group by 1,2,3

LOOP

INSERT INTO alert_summary(
statics_value, description, geographiczoneid, alerttypeid,
programid, periodid)
VALUES (rec_summary.rec_count, ' Emergency Requisitions', rec_summary.geographiczoneid, 'EMERGENCY_REQUISITION', rec_summary.programid, rec_summary.periodid);
end loop;


/*
 detail
*/

DELETE FROM alert_requisition_emergency;

FOR rec_detail IN
SELECT
rnrid,
CASE emergency WHEN true then 'Emergency' else 'Regular' end as req_type,
facilityname,
facilityid,
periodid,
programid,
geographiczoneid,
geographiczonename,
status
FROM
vw_facility_requisitions
where emergency = true

LOOP --fetch the table row inside the loop
select id into v_summaryid from alert_summary where geographiczoneid = rec_detail.geographiczoneid and programid = rec_detail.programid and periodid = rec_detail.periodid and alerttypeid = 'EMERGENCY_REQUISITION';

INSERT INTO alert_requisition_emergency(
alertsummaryid,programid, periodid, geographiczoneid, geographiczonename, rnrid, rnrtype, facilityid, status, facilityname)
VALUES (v_summaryid, rec_detail.programid, rec_detail.periodid, rec_detail.geographiczoneid, rec_detail.geographiczonename, rec_detail.rnrid, 'Emergency', rec_detail.facilityid, rec_detail.status, rec_detail.facilityname);
END LOOP;

RETURN msg ;
EXCEPTION
WHEN OTHERS THEN
RETURN 'fn_populate_alert_requisition_emergency - Error populating data. Please consult database administrtor. ' || SQLERRM ;
END ;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_alert_requisition_emergency()
  OWNER TO postgres;

