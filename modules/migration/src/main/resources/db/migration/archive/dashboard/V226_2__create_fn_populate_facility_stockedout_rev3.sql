--Function: fn_populate_alert_facility_stockedout()

DROP FUNCTION fn_populate_alert_facility_stockedout();

CREATE OR REPLACE FUNCTION fn_populate_alert_facility_stockedout()
  RETURNS character varying AS
$BODY$
DECLARE

rec_summary RECORD ;
rec_detail RECORD ;
msg CHARACTER VARYING (2000) ;
v_summaryid integer;

BEGIN
msg := 'Data saved successfully' ;
delete from alert_summary where alerttypeid = 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT';

/*
Summary
*/

FOR rec_summary IN
SELECT supervisorynodeid, programid, productid, product, periodid, count(facility_id) facility_count
FROM
vw_stock_status where indicator_product = true and status = 'SO'
group by 1, 2, 3, 4,5
LOOP
/*
 here we using static_value colum as peroductid for stockedout alert
*/
INSERT INTO alert_summary(
statics_value, description, supervisorynodeid, alerttypeid,
programid, periodid)
VALUES (rec_summary.productid,rec_summary.facility_count|| ' Facilities stocked out of ' ||rec_summary.product, rec_summary.supervisorynodeid, 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT', rec_summary.programid, rec_summary.periodid);
end loop;


/*
 detail
*/

DELETE FROM alert_facility_stockedout;
FOR rec_detail IN
SELECT *
FROM
vw_stock_status where indicator_product = true and status = 'SO'

LOOP --fetch the table row inside the loop
select id into v_summaryid from alert_summary where programid = rec_detail.programid and supervisorynodeid = rec_detail.supervisorynodeid and statics_value = rec_detail.productid and alerttypeid = 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT';

INSERT INTO alert_facility_stockedout(
alertsummaryid, facilityid, facilityname, productid, productname, stockoutdays, amc)
VALUES (v_summaryid, rec_detail.facility_id, rec_detail.facility, rec_detail.productid, rec_detail.product, rec_detail.stockoutdays, rec_detail.amc);
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
