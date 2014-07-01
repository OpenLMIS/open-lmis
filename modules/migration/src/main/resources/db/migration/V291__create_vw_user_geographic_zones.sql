DROP VIEW IF EXISTS vw_user_geographic_zones;

CREATE OR REPLACE VIEW vw_user_geographic_zones AS 

SELECT DISTINCT ra.userid AS userid,
    ra.supervisorynodeid,
    gz.id AS geographiczoneid,
    gz.levelid,
    ra.programid
FROM facilities f
JOIN geographic_zones gz on gz.id = f.geographiczoneid
JOIN requisition_group_members m on m.facilityId = f.id
JOIN requisition_groups rg on rg.id = m.requisitionGroupId
JOIN supervisory_nodes sn on sn.id = rg.supervisoryNodeId
JOIN role_assignments ra on (ra.supervisorynodeid = sn.id or ra.supervisorynodeid = sn.parentid)
JOIN geographic_zones d ON d.id = f.geographiczoneid   
WHERE ra.supervisorynodeid IS NOT NULL AND rg.supervisorynodeid IS NOT NULL;

ALTER TABLE vw_user_geographic_zones
  OWNER TO postgres;
