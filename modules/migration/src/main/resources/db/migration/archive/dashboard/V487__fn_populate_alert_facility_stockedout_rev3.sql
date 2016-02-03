-- Function: fn_populate_alert_facility_stockedout(integer)

DROP FUNCTION IF EXISTS fn_populate_alert_facility_stockedout(integer);

CREATE OR REPLACE FUNCTION fn_populate_alert_facility_stockedout(in_flag integer)
  RETURNS character varying AS
$BODY$
DECLARE
rec_summary RECORD ;
rec_detail RECORD ;
msg CHARACTER VARYING (2000) ;
v_summaryid integer;
v_this_run_date date;
v_last_run_date date;
BEGIN
msg := 'Success!!! fn_populate_alert_facility_stockedout.' ;
v_this_run_date = now()::date;
if in_flag = 1 then
v_last_run_date = (select modifieddate::date from dw_orders order by 1 desc limit 1);
else
v_last_run_date = (select now()::date - interval '12 month'::interval)::date;
end if;
delete from alert_summary where alerttypeid = 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT'
and  COALESCE(modifieddate,v_last_run_date) >= v_last_run_date;

delete from alert_facility_stockedout where COALESCE(modifieddate,v_last_run_date) >= v_last_run_date;

FOR rec_summary IN
select d.programid, d.periodid, d.geographiczoneid geoid, d.productid, d.productprimaryname product,
count(facilityid) facility_count
from dw_orders d
where stocking = 'S'
and tracer ='t'
and modifieddate::date >= v_last_run_date
GROUP BY
1, 2, 3, 4, 5
LOOP
INSERT INTO alert_summary(
statics_value, description, geographiczoneid, alerttypeid,programid, periodid, productid, modifieddate)
VALUES (rec_summary.facility_count,'Facilities stocked out of ' ||rec_summary.product, rec_summary.geoid, 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT', rec_summary.programid, rec_summary.periodid, rec_summary.productid,v_this_run_date);
end loop;
FOR rec_detail IN
select d.programid, d.periodid, d.geographiczoneid geoid,
d.geographiczonename, d.facilityid facility_id,
d.facilityname facility,
d.productid, d.productprimaryname product,
d.stockoutdays,
d.amc
from dw_orders d
where stocking = 'S'
and tracer = 't'
and modifieddate::date >= v_last_run_date
LOOP --fetch the table row inside the loop
select id into v_summaryid from alert_summary
where programid = rec_detail.programid
and periodid = rec_detail.periodid
and geographiczoneid = rec_detail.geoid
and productid = rec_detail.productid
and alerttypeid = 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT';
INSERT INTO alert_facility_stockedout(
alertsummaryid, programid, periodid, geographiczoneid, geographiczonename, facilityid, facilityname, productid, productname, stockoutdays, amc, modifieddate)
VALUES (v_summaryid, rec_detail.programid, rec_detail.periodid, rec_detail.geoid, rec_detail.geographiczonename, rec_detail.facility_id, rec_detail.facility, rec_detail.productid, rec_detail.product, rec_detail.stockoutdays, rec_detail.amc,v_this_run_date);
END LOOP;
RETURN msg ;
EXCEPTION
WHEN OTHERS THEN
RETURN 'Error!!! fn_populate_alert_facility_stockedout. ' || SQLERRM ;
END ;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_alert_facility_stockedout(integer)
  OWNER TO postgres;
