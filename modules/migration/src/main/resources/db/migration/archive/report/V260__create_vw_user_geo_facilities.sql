
 DROP VIEW IF EXISTS vw_user_geo_facilities;

CREATE OR REPLACE VIEW vw_user_geo_facilities AS 
 SELECT DISTINCT users.id AS userid,
    role_assignments.supervisorynodeid,
    geographic_zones.id AS geographiczoneid,
    facilities.id AS facilityid,
    facilities.name AS facilityname,
    facilities.enabled as facility_is_enabled,
    facilities.sdp as facility_is_sdp,
    facilities.mainphone,
    facilities.fax,
    facilities.active,
    facilities.typeid,    
    geographic_zones.levelid,
    geographic_zones.name AS geographiczonename    
   FROM role_assignments
   JOIN users ON role_assignments.userid = users.id
   JOIN requisition_groups ON role_assignments.supervisorynodeid = requisition_groups.supervisorynodeid
   JOIN requisition_group_members ON requisition_groups.id = requisition_group_members.requisitiongroupid
   JOIN facilities ON facilities.id = requisition_group_members.facilityid
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid;

ALTER TABLE vw_user_geo_facilities
  OWNER TO postgres;
