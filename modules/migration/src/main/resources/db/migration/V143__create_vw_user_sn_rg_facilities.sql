DROP VIEW IF EXISTS wv_user_sn_rg_facilities;

CREATE OR REPLACE VIEW wv_user_sn_rg_facilities AS 
 SELECT users.id AS userid,
    users.username,
    vw_user_supervisorynodes.id AS supervisorynodeid,
    vw_user_supervisorynodes.name AS supervisoryname,
    requisition_groups.id AS requisitiongroupid,
    requisition_groups.name AS requisitiongroupname,
    facilities.id AS facilityid,
    facilities.code AS facilitycode,
    facilities.name AS facilityname,
    facilities.active AS facilityactive
   FROM vw_user_supervisorynodes
   JOIN users ON users.id = vw_user_supervisorynodes.userid
   JOIN requisition_groups ON vw_user_supervisorynodes.id = requisition_groups.supervisorynodeid
   JOIN requisition_group_members ON requisition_groups.id = requisition_group_members.requisitiongroupid
   JOIN facilities ON facilities.id = requisition_group_members.facilityid;

ALTER TABLE wv_user_sn_rg_facilities
  OWNER TO postgres;