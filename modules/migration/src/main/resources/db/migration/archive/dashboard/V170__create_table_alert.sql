--Configuration table for dashboard alerts
DROP TABLE IF EXISTS alerts;
 
CREATE TABLE alerts
(
  alertTYpe character varying(50),
  display_section character varying(50),
  email boolean,
  sms boolean,
  detail_table character varying(50),
  sms_msg_template_key character varying(250),
  email_msg_template_key character varying(250)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE alerts
  OWNER TO postgres;


 --Sample alerts configuration
INSERT INTO "alerts" VALUES ('FACILITY_STOCKED_OUT','ALERT',true,true, 'alert_stockedout');
INSERT INTO "alerts" VALUES ('FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT','STOCKOUT',true,true, 'alert_stockedout');
INSERT INTO "alerts" VALUES ('EMERGENCY_REQUISITION','SUMMARY',true,true, 'emergency_requisitions');
INSERT INTO "alerts" VALUES ('REQUISITION_PENDING','ALERT',true,true, NULL);
INSERT INTO "alerts" VALUES ('COMMODITY_RATIONED','ALERT',true,true, NULL);
INSERT INTO "alerts" VALUES ('PRODUCT_RECALLED','ALERT',true,true, NULL);
INSERT INTO "alerts" VALUES ('UNSCHEDULED_ORDER','SUMMARY',true,true, NULL);
INSERT INTO "alerts" VALUES ('LATE_RESUPPLIED_FACILITY','SUMMARY',true,true, NULL);
INSERT INTO "alerts" VALUES ('FACILITY_WITH_EMPTY_POD','SUMMARY',true,true, NULL);