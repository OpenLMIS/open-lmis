-- Function: fn_populate_alert_requisition_rejected()

DROP FUNCTION IF EXISTS fn_populate_alert_requisition_rejected();

CREATE OR REPLACE FUNCTION fn_populate_alert_requisition_rejected()
  RETURNS character varying AS
$BODY$

 
 DECLARE

 rec RECORD ;

 rec2 RECORD ;

 rec_count INTEGER ;

 msg CHARACTER VARYING (2000) ;

 v_summaryid integer;
 v_supervisorynodeid integer;
 v_programid integer; 
 
 v_rnrid integer;
 v_rnrtype character varying(50);
 v_facilityid integer;
 v_facilityname character varying(50);
 v_current_periodid integer;


BEGIN

msg := 'Data saved successfully' ;

select max(periodid) into v_current_periodid from requisitions;
 
-- add summary record

FOR rec IN
 
SELECT
supervisorynodeid, programid, count(rnrid) rec_count
FROM
vw_facility_requisitions
where periodid = v_current_periodid and status = 'REJECTED'
group by 1,2


LOOP


delete from alert_summary where supervisorynodeid = rec.supervisorynodeid and programid = rec.programid and alerttypeid = 'ALERT_REQUISITION_REJECTED';
 

INSERT INTO alert_summary(

            statics_value, description, supervisorynodeid, alerttypeid,

            programid)

    VALUES (rec.rec_count, null, rec.supervisorynodeid, 'ALERT_REQUISITION_REJECTED', rec.program_id);

 

end loop;

 

 rec_count = 0;

-- add detail record

DELETE FROM alert_requisition_rejected;

FOR rec2 IN

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
where periodid = v_current_periodid and status = 'REJECTED'


LOOP --fetch the table row inside the loop

  select id into v_summaryid from alert_summary where programid = rec2.program_id and supervisorynodeid = rec2.supervisorynodeid;

  v_rnrid = rec2.req_id;
  v_rnrtype = rec.req_type;
  v_facilityid = rec2.facility_id;
  v_facilityname = rec2.facility_name;



 rec_count = rec_count + 1;

INSERT INTO alert_requisition_rejected(
            alertsummaryid, rnrid, rnrtype, facilityid, facilityname)
    VALUES (v_summaryid, v_rnrid, v_rnrtype, v_facilityid, v_facilityname);

 

  END LOOP;

RETURN msg ;

EXCEPTION

                WHEN OTHERS THEN

                                RETURN 'Error populating data. Please consult database administrtor. ' || SQLERRM ;

                END ;

 $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_alert_requisition_rejected()
  OWNER TO postgres;
