
DROP VIEW IF EXISTS vw_facility_requisitions;

CREATE OR REPLACE VIEW vw_facility_requisitions AS 
 SELECT facilities.id AS facilityid,
    facilities.code AS facilitycode,
    facilities.name AS facilityname,
    requisitions.id AS rnrid,
    requisitions.periodid,
    requisitions.status,
    facilities.geographiczoneid,
    facilities.enabled,
    facilities.sdp,
    facilities.typeid,
    requisitions.programid,
    requisitions.emergency,
    requisitions.createddate,
    geographic_zones.name AS geographiczonename
   FROM requisitions
   JOIN facilities ON facilities.id = requisitions.facilityid
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid;

ALTER TABLE vw_facility_requisitions
  OWNER TO postgres;

