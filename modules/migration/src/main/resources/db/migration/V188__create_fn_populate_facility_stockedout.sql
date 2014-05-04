DROP FUNCTION IF EXISTS fn_populate_alert_facility_stockedout();

CREATE OR REPLACE FUNCTION fn_populate_alert_facility_stockedout()
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

 

  v_facilityid integer;

 

  v_facilityname character varying(50);

 

  v_productid integer;

  v_productname character varying(150);
 

  v_stockoutdays integer;

 

  v_amc integer;

 

  v_current_periodid integer;

 

BEGIN

 

 

 

msg := 'Data saved successfully' ;

 

 

select max(periodid) into v_current_periodid from requisitions;

  
-- add summary record

 

FOR rec IN

 

SELECT fn_get_supervisorynodeid_by_facilityid(facility_id) supervisorynodeid, programid, count(facility_id) facilitycount

 

FROM

 

vw_stock_status where periodid = v_current_periodid and indicator_product = true and status = 'SO'

 

group by 1, 2

 

 

 

LOOP

 

 

 

delete from alert_summary where supervisorynodeid = rec.supervisorynodeid and programid = rec.programid and alerttypeid = 'FACILITY_STOCKED_OUT';

 
INSERT INTO alert_summary(

 

            statics_value, description, supervisorynodeid, alerttypeid,

 

            programid)

 

    VALUES (rec.facilitycount, null, rec.supervisorynodeid, 'FACILITY_STOCKED_OUT', rec.programid);

 

 

 

end loop;

 
rec_count = 0;

-- add detail record

DELETE FROM alert_facility_stockedout;

 

FOR rec2 IN

SELECT *

FROM

vw_stock_status where periodid = v_current_periodid and indicator_product = true and status = 'SO'


LOOP --fetch the table row inside the loop

 
  select id into v_summaryid from alert_summary where programid = rec2.programid and supervisorynodeid = rec2.supervisorynodeid;



  v_productid = rec2.productid;

  v_productname = rec2.product;
 

  v_facilityid = rec2.facility_id;

 

  v_facilityname = rec2.facility;

 

  v_stockoutdays = rec2.stockoutdays;

 

  v_amc = rec2.amc;

 

 

 

 rec_count = rec_count + 1;

 

 

 

 INSERT INTO alert_facility_stockedout(

 

            alertsummaryid, facilityid, facilityname, productid, productname, stockoutdays, amc)

 

    VALUES (v_summaryid, v_facilityid, v_facilityname, v_productid, v_productname, v_stockoutdays, v_amc);

 

 

 

  END LOOP;

 

RETURN msg ;

 

EXCEPTION

 

                WHEN OTHERS THEN

 

                                RETURN 'Error populating data. Please consult database administrtor. ' || SQLERRM ;

 

                END ;

 

 $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_alert_facility_stockedout()
  OWNER TO postgres;
