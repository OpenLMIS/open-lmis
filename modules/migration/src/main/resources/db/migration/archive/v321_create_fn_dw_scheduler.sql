fn_dw_sch-- Function: fn_dw_scheduler()

DROP FUNCTION IF EXISTS fn_dw_scheduler();

CREATE OR REPLACE FUNCTION fn_dw_scheduler()
  RETURNS character varying AS
$BODY$

DECLARE msg character varying(2000);

BEGIN

msg := 'Procedure completed successfully.';

delete from alert_summary;
select fn_populate_alert_facility_stockedout();
select fn_populate_alert_requisition_approved();
select fn_populate_alert_requisition_pending();
select fn_populate_alert_requisition_rejected();
select fn_populate_alert_requisition_emergency();

RETURN msg;

EXCEPTION WHEN OTHERS THEN
return SQLERRM;

END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_dw_scheduler()
  OWNER TO postgres;
