--stockedout facilities detail table
DROP TABLE IF EXISTS alert_stockedout;
CREATE TABLE alert_stockedout
(
  id SERIAL PRIMARY KEY,
  alertsummaryid integer,  
  facilityid integer,
  facilityname character varying(50),
  stockoutdays integer,
  amc integer,
  productid integer
)
WITH (
  OIDS=FALSE
);
ALTER TABLE alert_stockedout
  OWNER TO postgres;

INSERT INTO alert_stockedout(alertsummaryid,facilityid,facilityname,stockoutdays,amc,productid)
VALUES (1,  1, 'Facility 1', 5, 10,1415),
(1, 2, 'Facility 2', 12, 15,1420),
(1, 3, 'Facility 3', 30, 30,1445);
