
DROP FUNCTION IF EXISTS fn_populate_alert_requisition_approved();

CREATE OR REPLACE FUNCTION fn_populate_alert_requisition_approved()
  RETURNS character varying AS
$BODY$
DECLARE

rec_summary RECORD ;
rec_detail RECORD ;
msg CHARACTER VARYING (2000) ;
v_summaryid integer;

BEGIN
msg := 'fn_populate_alert_requisition_approved - Data saved successfully' ;
delete from alert_summary where alerttypeid = 'REQUISITION_APPROVED';

/*
 summary
*/
FOR rec_summary IN
SELECT
geographiczoneid, programid, periodid, count(rnrid) rnr_summary_count
FROM
vw_facility_requisitions
where  status = 'APPROVED'
group by 1,2,3
LOOP
/*
 first delete existing summary record
*/

INSERT INTO alert_summary(
statics_value, description, geographiczoneid, alerttypeid,programid,periodid)
VALUES (rec_summary.rnr_summary_count, 'Requisition Approved', rec_summary.geographiczoneid, 'REQUISITION_APPROVED', rec_summary.programid,rec_summary.periodid);
end loop;

/*
 detail
*/

DELETE FROM alert_requisition_approved;

FOR rec_detail IN
SELECT
rnrid,
CASE emergency WHEN true then 'Emergency' else 'Regular' end as req_type,
facilityname,
facilityid,
periodid,
programid,
geographiczoneid,
geographiczonename
FROM
vw_facility_requisitions
where status = 'APPROVED'

LOOP --fetch the table row inside the loop

select id into v_summaryid from alert_summary where geographiczoneid = rec_detail.geographiczoneid and programid = rec_detail.programid  and periodid = rec_detail.periodid and alerttypeid = 'REQUISITION_APPROVED';

INSERT INTO alert_requisition_approved(
alertsummaryid, programid, periodid, geographiczoneid, geographiczonename,rnrid, rnrtype, facilityid, facilityname)
VALUES (v_summaryid, rec_detail.programid, rec_detail.periodid, rec_detail.geographiczoneid, rec_detail.geographiczonename,  rec_detail.rnrid, rec_detail.req_type, rec_detail.facilityid, rec_detail.facilityname);
END LOOP;


RETURN msg ;
EXCEPTION
WHEN OTHERS THEN
RETURN 'fn_populate_alert_requisition_approved - Error populating data. Please consult database administrtor. ' || SQLERRM ;
END ;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_alert_requisition_approved()
  OWNER TO postgres;