-- View: vw_expected_facilities

DROP VIEW IF EXISTS vw_expected_facilities;

CREATE OR REPLACE VIEW vw_expected_facilities AS 
 SELECT 
    facilities.id AS facilityid,
    facilities.name AS facilityname,
    ps.programid,   
    pp.scheduleid,
    pp.id AS periodid,
    pp.name AS periodname,
    pp.startdate,
    pp.enddate,
    gz.id AS geographiczoneid,
    gz.name as geographiczonename
   FROM facilities
     JOIN programs_supported ps ON ps.facilityid = facilities.id
     JOIN geographic_zones gz ON gz.id = facilities.geographiczoneid
     JOIN requisition_group_members rgm ON rgm.facilityid = facilities.id
     JOIN requisition_group_program_schedules rgps ON rgps.requisitiongroupid = rgm.requisitiongroupid AND rgps.programid = ps.programid
     JOIN processing_periods pp ON pp.scheduleid = rgps.scheduleid
  WHERE gz.levelid = (( SELECT max(geographic_levels.id) AS max
           FROM geographic_levels));

ALTER TABLE vw_expected_facilities
  OWNER TO postgres;

