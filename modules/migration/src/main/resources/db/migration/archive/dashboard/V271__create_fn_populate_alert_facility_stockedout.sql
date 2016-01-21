
DROP FUNCTION IF EXISTS fn_populate_alert_facility_stockedout();

CREATE OR REPLACE FUNCTION fn_populate_alert_facility_stockedout()
  RETURNS character varying AS
$BODY$
DECLARE

rec_summary RECORD ;
rec_detail RECORD ;
msg CHARACTER VARYING (2000) ;
v_summaryid integer;

BEGIN
msg := 'fn_populate_alert_facility_stockedout- Data saved successfully' ;
delete from alert_summary where alerttypeid = 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT';

/*
Summary
*/

FOR rec_summary IN
SELECT
vw_stock_status_2.programid,
vw_stock_status_2.periodid,
vw_stock_status_2.gz_id as geoid,
vw_stock_status_2.productid,
vw_stock_status_2.product,
Count(vw_stock_status_2.facility_id) AS facility_count
FROM
vw_stock_status_2
WHERE
vw_stock_status_2.indicator_product = true AND
vw_stock_status_2.status = 'SO'
GROUP BY
1, 2, 3, 4, 5
LOOP


/*
 here we using static_value colum as peroductid for stockedout alert
*/
INSERT INTO alert_summary(
statics_value, description, geographiczoneid, alerttypeid,programid, periodid, productid)
VALUES (rec_summary.facility_count,'Facilities stocked out of ' ||rec_summary.product, rec_summary.geoid, 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT', rec_summary.programid, rec_summary.periodid, rec_summary.productid);
end loop;


/*
 detail
*/

DELETE FROM alert_facility_stockedout;

FOR rec_detail IN
SELECT
vw_stock_status_2.programid,
vw_stock_status_2.periodid,
vw_stock_status_2.gz_id as geoid,
vw_stock_status_2.location as geographiczonename,
vw_stock_status_2.facility_id,
vw_stock_status_2.facility,
vw_stock_status_2.productid,
vw_stock_status_2.product,
vw_stock_status_2.stockoutdays, 
vw_stock_status_2.amc

FROM
vw_stock_status_2
WHERE
vw_stock_status_2.indicator_product = true AND
vw_stock_status_2.status = 'SO'


LOOP --fetch the table row inside the loop
select id into v_summaryid from alert_summary where programid = rec_detail.programid and geographiczoneid = rec_detail.geoid and productid = rec_detail.productid and alerttypeid = 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT';

INSERT INTO alert_facility_stockedout(
alertsummaryid, programid, periodid, geographiczoneid, geographiczonename, facilityid, facilityname, productid, productname, stockoutdays, amc)
VALUES (v_summaryid, rec_detail.programid, rec_detail.periodid, rec_detail.geoid, rec_detail.geographiczonename, rec_detail.facility_id, rec_detail.facility, rec_detail.productid, rec_detail.product, rec_detail.stockoutdays, rec_detail.amc);
END LOOP;

RETURN msg ;
EXCEPTION
WHEN OTHERS THEN
RETURN 'fn_populate_alert_facility_stockedout - Error populating data. Please consult database administrtor. ' || SQLERRM ;
END ;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_alert_facility_stockedout()
  OWNER TO postgres;