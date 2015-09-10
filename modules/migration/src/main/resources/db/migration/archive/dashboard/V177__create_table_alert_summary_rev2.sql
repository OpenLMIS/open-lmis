DROP TABLE IF EXISTS alert_summary;
 
CREATE TABLE  alert_summary
(
  id SERIAL PRIMARY KEY,
  statics_value integer,
  description character varying(2000),  
  supervisorynodeId integer,
  alertTypeId character varying(50),
  programid integer
)
WITH (
  OIDS=FALSE
);
ALTER TABLE  alert_summary
  OWNER TO postgres;

 --sample alerts data
INSERT INTO alert_summary(statics_value,description,supervisorynodeId,alertTypeId,programid)
 VALUES (10,'facilities stocked out',312,'FACILITY_STOCKED_OUT',1),
 (5,'facilities stocked out of tracer product X',312,'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT',2),
 (15,'emergency requisitions',312,'EMERGENCY_REQUISITION',1),
 (25,'requisitions pending',1,'REQUISITION_PENDING',2),
 (2,'commodities have been rationed',2,'COMMODITY_RATIONED',2),
 (3,'products have been recalled',312,'PRODUCT_RECALLED',2),
 (12,'unscheduled orders',1,'UNSCHEDULED_ORDER',1),
 (5,'% facilities have been resupplied later than X days',2,'LATE_RESUPPLIED_FACILITY',1),
 (70,E'% facilities have not filled in their POD\'s',3,'FACILITY_WITH_EMPTY_POD',1);

