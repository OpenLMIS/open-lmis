DROP VIEW IF EXISTS vw_rnr_status;
CREATE OR REPLACE VIEW vw_rnr_status AS 
 SELECT p.name AS programname, r.programid, r.periodid, rg.id AS requisitiongroupid, f.id AS facilityid, r.id AS rnrid, r.status
   FROM facilities f
   JOIN requisitions r ON r.facilityid = f.id
   JOIN programs p ON p.id = r.programid
   JOIN requisition_group_members ON requisition_group_members.facilityid = f.id
   JOIN requisition_groups rg ON rg.id = requisition_group_members.requisitiongroupid
   JOIN requisition_status_changes ON r.id = requisition_status_changes.rnrid;

ALTER TABLE vw_rnr_status
  OWNER TO postgres;