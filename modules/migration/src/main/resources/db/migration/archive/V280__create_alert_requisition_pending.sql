
DROP TABLE IF EXISTS alert_requisition_pending;

CREATE TABLE alert_requisition_pending
(
  id serial NOT NULL,
  alertsummaryid integer,
  programid integer,
  periodid integer,
  geographiczoneid integer,
  geographiczonename character varying(250),
  rnrid integer,
  rnrtype character varying(50),
  facilityid integer,
  facilityname character varying(50),
  CONSTRAINT alert_requisition_pending_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE alert_requisition_pending
  OWNER TO postgres;

