-- delete unused stored procedure in datawarehouse
DROP FUNCTION IF EXISTS fn_populate_alert_facility_stockedout();
DROP FUNCTION IF EXISTS fn_populate_alert_requisition_approved();
DROP FUNCTION IF EXISTS fn_populate_alert_requisition_emergency();
DROP FUNCTION IF EXISTS fn_populate_alert_requisition_pending();
DROP FUNCTION IF EXISTS fn_populate_alert_requisition_rejected();
DROP FUNCTION IF EXISTS fn_populate_dw_orders(integer[]);
DROP FUNCTION IF EXISTS fn_populate_dw_orders_2(integer[]);
