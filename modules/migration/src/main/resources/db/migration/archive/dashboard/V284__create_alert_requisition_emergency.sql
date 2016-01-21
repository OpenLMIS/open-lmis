
DROP TABLE IF EXISTS alert_requisition_emergency;

CREATE TABLE alert_requisition_emergency
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
  status character varying(50),
  facilityname character varying(50),
  CONSTRAINT alert_requisition_emergency_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE alert_requisition_emergency
  OWNER TO postgres;

