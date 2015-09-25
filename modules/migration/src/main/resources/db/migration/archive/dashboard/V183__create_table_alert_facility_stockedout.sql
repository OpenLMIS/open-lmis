-- Table: alert_facility_stockedout

DROP TABLE IF EXISTS alert_facility_stockedout;

CREATE TABLE alert_facility_stockedout
(
  id serial NOT NULL,
  alertsummaryid integer,
  facilityid integer,
  facilityname character varying(50),
  productid integer,
  productname character varying(150),
  stockoutdays integer,
  amc integer,
  CONSTRAINT alert_facility_stockedout_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE alert_facility_stockedout
  OWNER TO postgres;