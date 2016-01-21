
DROP VIEW IF EXISTS  vw_regimen_summary CASCADE;

CREATE OR REPLACE VIEW vw_regimen_summary AS

 SELECT DISTINCT li.id, li.name AS regimen,
    r.programid, programs.name AS program, li.code,
    gz.name AS district, gz.id AS zoneid, f.name AS facility,
    li.patientsontreatment, li.patientstoinitiatetreatment,
    li.patientsstoppedtreatment, li.regimendisplayorder,
    li.regimencategorydisplayorder, li.regimencategory, li.rnrid,
    pp.name AS period, requisition_group_members.id AS rgroupid,
    rg.name AS rgroup, r.id AS req_id, pp.id AS periodid, ps.id AS scheduleid,
    ps.name AS schedule,r.status status
   FROM regimen_line_items li
   JOIN requisitions r ON r.id = li.rnrid
   JOIN facilities f ON r.facilityid = f.id
   JOIN facility_types ft ON ft.id = f.typeid
   JOIN geographic_zones gz ON gz.id = f.geographiczoneid
   JOIN programs ON r.programid = programs.id
   JOIN requisition_group_members ON requisition_group_members.facilityid = f.id
   JOIN requisition_groups rg ON rg.id = requisition_group_members.requisitiongroupid
   JOIN requisition_group_program_schedules rgps ON rgps.programid = programs.id AND rgps.requisitiongroupid = rg.id
   JOIN processing_periods pp ON pp.id = r.periodid
   JOIN processing_schedules ps ON ps.id = rgps.scheduleid
   JOIN supply_lines sl ON sl.supervisorynodeid = r.supervisorynodeid AND r.programid = sl.programid
   JOIN facilities s ON s.id = sl.supplyingfacilityid