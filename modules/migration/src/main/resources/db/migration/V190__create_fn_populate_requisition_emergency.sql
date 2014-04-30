-- Function: fn_populate_alert_requisition_emergency()

DROP FUNCTION IF EXISTS fn_populate_alert_requisition_emergency();

CREATE OR REPLACE FUNCTION fn_populate_alert_requisition_emergency()
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
 v_status character varying(50);
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
where periodid = v_current_periodid and emergency = true
group by 1,2



LOOP


delete from alert_summary where supervisorynodeid = rec.supervisorynodeid and programid = rec.programid and alerttypeid = 'EMERGENCY_REQUISITION';
 

INSERT INTO alert_summary(

            statics_value, description, supervisorynodeid, alerttypeid,

            programid)

    VALUES (rec.rec_count, null, rec.supervisorynodeid, 'EMERGENCY_REQUISITION', rec.programid);

 

end loop;

 

 rec_count = 0;

-- add detail record

DELETE FROM alert_requisition_emergency;

FOR rec2 IN

 
SELECT
rnrid,
CASE emergency WHEN true then 'Emergency' else 'Regular' end as req_type,
facilityname,
facilityid,
periodid,
programid,
supervisorynodeid,
status

FROM
vw_facility_requisitions
where periodid = v_current_periodid and emergency = true


LOOP --fetch the table row inside the loop

 

  select id into v_summaryid from alert_summary where programid = rec2.programid and supervisorynodeid = rec2.supervisorynodeid;

  v_rnrid = rec2.rnrid;
  v_rnrtype = 'Emergency';
  v_facilityid = rec2.facilityid;
  v_facilityname = rec2.facilityname;
  v_status = rec2.status;


 rec_count = rec_count + 1;

 INSERT INTO alert_requisition_emergency(
            alertsummaryid, rnrid, rnrtype, facilityid, status, facilityname)
    VALUES (v_summaryid, v_rnrid, v_rnrtype, v_facilityid, v_status, v_facilityname);

 

  END LOOP;

RETURN msg ;

EXCEPTION

                WHEN OTHERS THEN

                                RETURN 'Error populating data. Please consult database administrtor. ' || SQLERRM ;

                END ;

 $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_alert_requisition_emergency()
  OWNER TO postgres;
