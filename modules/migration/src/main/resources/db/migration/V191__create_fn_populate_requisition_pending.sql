-- Function: fn_populate_alert_requisition_pending()

DROP FUNCTION IF EXISTS fn_populate_alert_requisition_pending();

CREATE OR REPLACE FUNCTION fn_populate_alert_requisition_pending()
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
where periodid = v_current_periodid and  status = 'IN_APPROVAL'
group by 1,2


LOOP


delete from alert_summary where supervisorynodeid = rec.supervisorynodeid and programid = rec.programid and alerttypeid = 'ALERT_REQUISITION_PENDING';
 

INSERT INTO alert_summary(

            statics_value, description, supervisorynodeid, alerttypeid,

            programid)

    VALUES (rec.rec_count, null, rec.supervisorynodeid, 'ALERT_REQUISITION_PENDING', rec.programid);

 

end loop;

 

 rec_count = 0;

-- add detail record

DELETE FROM alert_requisition_pending;

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
where periodid = v_current_periodid and status = 'IN_APPROVAL'




LOOP --fetch the table row inside the loop

 

  select id into v_summaryid from alert_summary where programid = rec2.programid and supervisorynodeid = rec2.supervisorynodeid;

  v_rnrid = rec2.rnrid;
  v_rnrtype = rec.req_type;
  v_facilityid = rec2.facilityid;
  v_facilityname = rec2.facilityname;



 rec_count = rec_count + 1;

INSERT INTO alert_requisition_pending(
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
ALTER FUNCTION fn_populate_alert_requisition_pending()
  OWNER TO postgres;
