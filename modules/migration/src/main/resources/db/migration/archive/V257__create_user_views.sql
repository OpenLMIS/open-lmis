DROP VIEW IF EXISTS vw_user_facilities;

CREATE VIEW vw_user_facilities
AS
SELECT DISTINCT f.id facility_id, f.geographicZoneId as district_id , rg.id requisition_group_id, ra.userid user_id, ra.programid program_id from facilities f
					join requisition_group_members m on m.facilityId = f.id
					join requisition_groups rg on rg.id = m.requisitionGroupId
					join supervisory_nodes sn on sn.id = rg.supervisoryNodeId
					join role_assignments ra on (ra.supervisorynodeid = sn.id or ra.supervisorynodeid = sn.parentid)
;

DROP VIEW IF EXISTS vw_user_districts;

CREATE VIEW vw_user_districts
AS
SELECT DISTINCT user_id, district_id, program_id
	from vw_user_facilities
;