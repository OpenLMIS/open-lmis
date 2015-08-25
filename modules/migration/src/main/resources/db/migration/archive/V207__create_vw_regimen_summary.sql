
DROP VIEW IF EXISTS  vw_regimen_summary;

CREATE OR REPLACE VIEW vw_regimen_summary AS
 SELECT DISTINCT r.programid, ps.id AS scheduleid, pp.id AS periodid, rg.id AS rgroupid, li.regimencategory, regimens.categoryid, regimens.id AS regimenid, regimens.name AS regimen, li.patientsontreatment, li.patientstoinitiatetreatment, li.patientsstoppedtreatment
   FROM regimen_line_items li
   JOIN requisitions r ON r.id = li.rnrid
   JOIN processing_periods pp ON pp.id = r.periodid
   JOIN processing_schedules ps ON ps.id = pp.scheduleid
   JOIN requisition_group_members ON requisition_group_members.facilityid = r.facilityid
   JOIN requisition_groups rg ON rg.id = requisition_group_members.requisitiongroupid
   JOIN regimens ON regimens.programid = r.programid;

ALTER TABLE vw_regimen_summary
  OWNER TO postgres;