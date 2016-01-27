DROP VIEW IF EXISTS vw_number_rnr_created_by_facility;

CREATE OR REPLACE VIEW vw_number_rnr_created_by_facility AS
 SELECT count(r.status) AS totalstatus, r.status, rg.id AS requisitiongroupid
   FROM facilities f
   JOIN requisitions r ON r.facilityid = f.id
   JOIN programs p ON p.id = r.programid
   JOIN requisition_group_members ON requisition_group_members.facilityid = f.id
   JOIN requisition_groups rg ON rg.id = requisition_group_members.requisitiongroupid
  WHERE (r.id IN ( SELECT requisition_status_changes.rnrid
   FROM requisition_status_changes
  GROUP BY requisition_status_changes.rnrid, requisition_status_changes.status
 HAVING count(*) > 0))
  GROUP BY r.status, rg.id
  ORDER BY r.status;

ALTER TABLE vw_number_rnr_created_by_facility
  OWNER TO postgres;

