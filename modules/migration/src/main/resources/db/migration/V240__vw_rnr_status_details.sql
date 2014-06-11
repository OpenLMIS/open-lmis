
   DROP VIEW IF EXISTS vw_rnr_status_details;

CREATE OR REPLACE VIEW vw_rnr_status_details AS
 SELECT p.name AS programname, r.programid, r.periodid, ps.name AS periodname, rg.id AS requisitiongroupid, r.createddate, f.code AS facilitycode, f.name AS facilityname, rg.name AS requisitiongroup, f.id AS facilityid, r.id AS rnrid, r.status, ft.name AS facilitytypename
   FROM facilities f
   JOIN requisitions r ON r.facilityid = f.id
   JOIN programs p ON p.id = r.programid
   JOIN requisition_group_members ON requisition_group_members.facilityid = f.id
   JOIN requisition_groups rg ON rg.id = requisition_group_members.requisitiongroupid
   JOIN processing_periods ps ON ps.id = r.periodid
   JOIN requisition_status_changes ON r.id = requisition_status_changes.rnrid
   JOIN facility_types ft ON ft.id = f.typeid;