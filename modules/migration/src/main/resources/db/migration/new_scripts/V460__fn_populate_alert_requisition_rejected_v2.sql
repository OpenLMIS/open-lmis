-- Function: fn_populate_alert_requisition_rejected(integer)

DROP FUNCTION IF EXISTS fn_populate_alert_requisition_rejected(integer);

CREATE OR REPLACE FUNCTION fn_populate_alert_requisition_rejected(in_flag integer)
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

msg := 'Success!!! fn_populate_alert_requisition_rejected.' ;
v_this_run_date = now()::date;

-- date dw_orders was last updataed 
if in_flag = 1 then
 v_last_run_date = (select modifieddate::date from dw_orders order by 1 desc limit 1);
else
 v_last_run_date = (select now()::date - interval '12 month'::interval)::date;
end if;


-- delete from last run date. We will recreated them.
delete from alert_summary where alerttypeid = 'REQUISITION_REJECTED' 
and  COALESCE(modifieddate,v_last_run_date) >= v_last_run_date;

-- get Pending data and stored in summary table
FOR rec_summary IN
select d.geographiczoneid, d.programid, d.periodid, count(distinct rnrid) rnr_summary_count from dw_orders d 
where status = 'REJECTED'
and modifieddate::date >= v_last_run_date
group by 1,2,3

LOOP
INSERT INTO alert_summary(
statics_value, description, geographiczoneid, alerttypeid,programid,periodid,modifieddate)
VALUES (rec_summary.rnr_summary_count, 'Requisition rejected', rec_summary.geographiczoneid, 
        'REQUISITION_REJECTED', rec_summary.programid,rec_summary.periodid, v_this_run_date);
end loop;

DELETE FROM alert_requisition_rejected 
where COALESCE(modifieddate,v_last_run_date) >= v_last_run_date;

-- get detail rejected data and stored in detail table
FOR rec_detail IN

select d.rnrid, CASE d.emergency WHEN true then 'Emergency' else 'Regular' end as req_type,
d.facilityname, d.facilityid,d.periodid, d.programid, d.geographiczoneid, d.geographiczonename
from dw_orders d 
where status = 'REJECTED'
and modifieddate::date >= v_last_run_date


LOOP 
-- get summary id
select id into v_summaryid from alert_summary 
where geographiczoneid = rec_detail.geographiczoneid 
and programid = rec_detail.programid  
and periodid = rec_detail.periodid 
and alerttypeid = 'REQUISITION_REJECTED';


INSERT INTO alert_requisition_rejected(
alertsummaryid, programid, periodid, geographiczoneid, geographiczonename,rnrid, rnrtype, facilityid, facilityname,modifieddate)
VALUES (v_summaryid, rec_detail.programid, rec_detail.periodid, rec_detail.geographiczoneid, rec_detail.geographiczonename,  rec_detail.rnrid, rec_detail.req_type, rec_detail.facilityid, rec_detail.facilityname,v_this_run_date);
END LOOP;

RETURN msg ;

EXCEPTION
WHEN OTHERS THEN
RETURN 'Error!!! fn_populate_alert_requisition_rejected. ' || SQLERRM ;
END ;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_alert_requisition_rejected(integer)
  OWNER TO openlmis;
