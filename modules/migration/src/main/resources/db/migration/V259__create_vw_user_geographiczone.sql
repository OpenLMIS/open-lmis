
DROP VIEW IF EXISTS vw_user_geographic_zones;

CREATE OR REPLACE VIEW vw_user_geographic_zones AS 
 SELECT DISTINCT users.id AS userid,
    role_assignments.supervisorynodeid,
    geographic_zones.id AS geographiczoneid,
    geographic_zones.levelid
   FROM role_assignments
   JOIN users ON role_assignments.userid = users.id
   JOIN requisition_groups ON role_assignments.supervisorynodeid = requisition_groups.supervisorynodeid
   JOIN requisition_group_members ON requisition_groups.id = requisition_group_members.requisitiongroupid
   JOIN facilities ON facilities.id = requisition_group_members.facilityid
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
  WHERE role_assignments.supervisorynodeid IS NOT NULL AND requisition_groups.supervisorynodeid IS NOT NULL;

ALTER TABLE vw_user_geographic_zones
  OWNER TO postgres;