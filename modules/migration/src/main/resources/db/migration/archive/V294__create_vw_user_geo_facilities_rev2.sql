-- View: vw_user_geo_facilities

DROP VIEW IF EXISTS vw_user_geo_facilities;

CREATE OR REPLACE VIEW vw_user_geo_facilities AS 
 SELECT role_assignments.userid, role_assignments.supervisorynodeid, 
    geographic_zones.id AS geographiczoneid, facilities.id AS facilityid, 
    facilities.name AS facilityname, facilities.enabled AS facility_is_enabled, 
    facilities.sdp AS facility_is_sdp, facilities.mainphone, facilities.fax, 
    facilities.active, facilities.typeid, geographic_zones.levelid, 
    geographic_zones.name AS geographiczonename
   FROM facilities
   JOIN requisition_group_members ON facilities.id = requisition_group_members.facilityid
   JOIN requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid
   JOIN supervisory_nodes ON supervisory_nodes.id = requisition_groups.supervisorynodeid
   JOIN role_assignments ON supervisory_nodes.id = role_assignments.supervisorynodeid OR role_assignments.supervisorynodeid = supervisory_nodes.parentid
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid;

ALTER TABLE vw_user_geo_facilities
  OWNER TO postgres;