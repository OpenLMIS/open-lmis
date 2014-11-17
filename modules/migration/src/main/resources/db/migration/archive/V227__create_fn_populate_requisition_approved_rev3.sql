-- Function: fn_populate_alert_requisition_approved()

DROP FUNCTION fn_populate_alert_requisition_approved();

CREATE OR REPLACE FUNCTION fn_populate_alert_requisition_approved()
  RETURNS character varying AS
$BODY$
DECLARE

rec_summary RECORD ;
rec_detail RECORD ;
msg CHARACTER VARYING (2000) ;
v_summaryid integer;

BEGIN
msg := 'Data saved successfully' ;

/*
 summary
*/
FOR rec_summary IN
SELECT
supervisorynodeid, programid, periodid, count(rnrid) rnr_summary_count
FROM
vw_facility_requisitions
where  status = 'APPROVED'
group by 1,2,3
LOOP
/*
 first delete existing summary record
*/
delete from alert_summary where supervisorynodeid = rec_summary.supervisorynodeid and programid = rec_summary.programid and periodid =  rec_summary.periodid and alerttypeid = 'REQUISITION_APPROVED';

INSERT INTO alert_summary(
statics_value, description, supervisorynodeid, alerttypeid,
programid,periodid)
VALUES (rec_summary.rnr_summary_count, 'Requisition Approved', rec_summary.supervisorynodeid, 'REQUISITION_APPROVED', rec_summary.programid,rec_summary.periodid);
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
supervisorynodeid
FROM
vw_facility_requisitions
where status = 'APPROVED'

LOOP --fetch the table row inside the loop

select id into v_summaryid from alert_summary where supervisorynodeid = rec_detail.supervisorynodeid and programid = rec_detail.programid  and periodid = rec_detail.periodid and alerttypeid = 'REQUISITION_APPROVED';

INSERT INTO alert_requisition_approved(
alertsummaryid, rnrid, rnrtype, facilityid, facilityname)
VALUES (v_summaryid, rec_detail.rnrid, rec_detail.req_type, rec_detail.facilityid, rec_detail.facilityname);
END LOOP;

RETURN msg ;
EXCEPTION
WHEN OTHERS THEN
RETURN 'Error populating data. Please consult database administrtor. ' || SQLERRM ;
END ;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_alert_requisition_approved()
  OWNER TO postgres;
