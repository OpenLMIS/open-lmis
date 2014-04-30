-- View: vw_facility_requisitions

DROP VIEW IF EXISTS vw_facility_requisitions;

CREATE OR REPLACE VIEW vw_facility_requisitions AS 
 SELECT facilities.id AS facilityid,
    facilities.code AS facilitycode,
    facilities.name AS facilityname,
    requisitions.id AS rnrid,
    requisitions.periodid,
    requisitions.status,
    fn_get_supervisorynodeid_by_facilityid(requisitions.facilityid) AS supervisorynodeid,
    requisitions.programid,
    requisitions.emergency
   FROM requisitions
   JOIN facilities ON facilities.id = requisitions.facilityid;

ALTER TABLE vw_facility_requisitions
  OWNER TO postgres;
