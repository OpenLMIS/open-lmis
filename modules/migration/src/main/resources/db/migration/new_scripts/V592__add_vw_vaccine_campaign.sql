-- View: vw_vaccine_campaign

DROP VIEW IF EXISTS vw_vaccine_campaign;

CREATE OR REPLACE VIEW vw_vaccine_campaign AS 
 SELECT gz.id AS geographic_zone_id,
    gz.name AS geographic_zone_name,
    gz.levelid AS level_id,
    gz.parentid AS parent_id,
    f.code AS facility_code,
    f.name AS facility_name,
    pp.id AS period_id,
    pp.name AS period_name,
    vr.id AS report_id,
    vr.programid AS program_id,
    vr.facilityid AS facility_id,
    vr.status,
    camp.name AS camp_name,
    camp.venue AS camp_venue,
    camp.startdate AS camp_start_date,
    camp.enddate AS camp_end_date,
    camp.vaccinated AS camp_vaccinated,
    camp.childrenvaccinated AS camp_childrenvaccinated,
    camp.pregnantwomanvaccinated AS camp_pregnantwomanvaccinated,
    camp.remarks AS camp_remarks,
    camp.otherobjectives AS camp_other
    FROM vaccine_report_campaign_line_items camp
    JOIN vaccine_reports vr ON camp.reportid = vr.id JOIN
    processing_periods pp ON vr.periodid = pp.id JOIN facilities f ON
    vr.facilityid = f.id JOIN geographic_zones gz ON
    f.geographiczoneid = gz.id;