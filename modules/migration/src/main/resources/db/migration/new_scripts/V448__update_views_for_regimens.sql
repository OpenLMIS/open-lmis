
DROP VIEW IF EXISTS vw_regimen_district_distribution;
DROP VIEW IF EXISTS vw_regimen_summary;

CREATE OR REPLACE VIEW vw_regimen_summary AS
 SELECT r.programid,
    ps.id AS scheduleid,
    pp.id AS periodid,
    li.regimencategory,
    regimens.categoryid,
    regimens.id AS regimenid,
    regimens.name AS regimen,
    li.patientsontreatment,
    li.patientstoinitiatetreatment,
    li.patientsstoppedtreatment,
    r.status,
    d.district_id AS geographiczoneid,
    d.district_name district
   FROM regimen_line_items li
     JOIN requisitions r ON li.rnrid = r.id
     JOIN processing_periods pp ON r.periodid = pp.id
     JOIN processing_schedules ps ON ps.id = pp.scheduleid
     JOIN programs_supported pps ON r.programid = pps.programid AND r.facilityid = pps.facilityid
     JOIN regimens ON li.code::text = regimens.code::text
     JOIN facilities ON r.facilityid = facilities.id
     JOIN vw_districts d ON facilities.geographiczoneid = d.district_id;


CREATE OR REPLACE VIEW vw_regimen_district_distribution AS
 SELECT r.programid,
    rgps.scheduleid,
    pp.id AS periodid,
    regimens.categoryid,
    regimens.id AS regimenid,
    li.name AS regimen,
    li.patientsontreatment,
    li.patientstoinitiatetreatment,
    li.patientsstoppedtreatment,
    r.facilityid,
    r.status,
    f.name AS facilityname,
    f.code as facilitycode,
    f.typeid AS facilitytypeid,
    ft.name facilitytype,
    d.district_name AS district,
    d.district_id AS districtid,
    d.region_id AS regionid,
    d.region_name region,
    d.zone_id AS zoneid,
    d.zone_name AS zone,
    d.parent
   FROM regimen_line_items li
     JOIN requisitions r ON li.rnrid = r.id
     JOIN facilities f ON r.facilityid = f.id
     JOIN facility_types ft ON f.typeid = ft.id
     JOIN vw_districts d ON f.geographiczoneid = d.district_id
     JOIN requisition_group_members rgm ON r.facilityid = rgm.facilityid
     JOIN programs_supported ps ON r.programid = ps.programid AND r.facilityid = ps.facilityid
     JOIN regimens ON li.code::text = regimens.code::text
     JOIN processing_periods pp ON r.periodid = pp.id
     JOIN requisition_group_program_schedules rgps ON rgm.requisitiongroupid = rgps.requisitiongroupid AND pp.scheduleid = rgps.scheduleid;

ALTER TABLE vw_regimen_district_distribution OWNER TO postgres;
ALTER TABLE vw_regimen_summary OWNER TO postgres;

