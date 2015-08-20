-- Function: fn_populate_alert_requisition_approved(integer)

DROP FUNCTION IF EXISTS fn_populate_alert_requisition_approved(integer);

CREATE OR REPLACE FUNCTION fn_populate_alert_requisition_approved(in_flag integer)
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

msg := 'Success!!! fn_populate_alert_requisition_approved' ;
v_this_run_date = now()::date;

-- date dw_orders was last updataed 
if in_flag = 1 then
 v_last_run_date = (select modifieddate::date from dw_orders order by 1 desc limit 1);
else
 v_last_run_date = (select now()::date - interval '12 month'::interval)::date;
end if;


-- delete from last run date. We will recreated them.
delete from alert_summary where alerttypeid = 'REQUISITION_APPROVED' 
and  COALESCE(modifieddate,v_last_run_date) >= v_last_run_date;

delete from alert_requisition_approved where alertsummaryid not in (select alertsummaryid from alert_summary);

-- get Pending data and stored in summary table
FOR rec_summary IN
select d.geographiczoneid, d.programid, d.periodid, count(distinct rnrid) rnr_summary_count from dw_orders d 
where status = 'APPROVED'
and modifieddate::date >= v_last_run_date
group by 1,2,3

LOOP
INSERT INTO alert_summary(
statics_value, description, geographiczoneid, alerttypeid,programid,periodid,modifieddate)
VALUES (rec_summary.rnr_summary_count, 'Requisition approved', rec_summary.geographiczoneid, 
        'REQUISITION_APPROVED', rec_summary.programid,rec_summary.periodid, v_this_run_date);
end loop;

DELETE FROM alert_requisition_approved 
where COALESCE(modifieddate,v_last_run_date) >= v_last_run_date;

-- get detail approved data and stored in detail table
FOR rec_detail IN

select d.rnrid, CASE d.emergency WHEN true then 'Emergency' else 'Regular' end as req_type,
d.facilityname, d.facilityid,d.periodid, d.programid, d.geographiczoneid, d.geographiczonename
from dw_orders d 
where status = 'APPROVED'
and modifieddate::date >= v_last_run_date


LOOP 
-- get summary id
select id into v_summaryid from alert_summary 
where geographiczoneid = rec_detail.geographiczoneid 
and programid = rec_detail.programid  
and periodid = rec_detail.periodid 
and alerttypeid = 'REQUISITION_APPROVED';


INSERT INTO alert_requisition_approved(
alertsummaryid, programid, periodid, geographiczoneid, geographiczonename,rnrid, rnrtype, facilityid, facilityname,modifieddate)
VALUES (v_summaryid, rec_detail.programid, rec_detail.periodid, rec_detail.geographiczoneid, rec_detail.geographiczonename,  rec_detail.rnrid, rec_detail.req_type, rec_detail.facilityid, rec_detail.facilityname,v_this_run_date);
END LOOP;

RETURN msg ;

EXCEPTION
WHEN OTHERS THEN
RETURN 'Error!!! fn_populate_alert_requisition_approved. ' || SQLERRM ;
END ;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_alert_requisition_approved(integer)
  OWNER TO openlmis;
