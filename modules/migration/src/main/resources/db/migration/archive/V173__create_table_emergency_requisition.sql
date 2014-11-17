
DROP TABLE IF EXISTS emergency_requisitions;
CREATE TABLE emergency_requisitions
(
  id SERIAL PRIMARY KEY,
  alertsummaryid integer,  
  rnrId integer,
  facilityid integer,
  status character varying(50)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE emergency_requisitions
  OWNER TO postgres;

INSERT INTO emergency_requisitions(alertsummaryid,rnrid,facilityid,status)
VALUES (3, 312, 5,'INITIATED'),
(3, 1,2,'SUBMITTED'),
(3, 1,2,'SUBMITTED'),
(3, 1,2,'SUBMITTED'),
(3, 2, 8,'APPROVED'),
(3, 2, 8,'APPROVED');