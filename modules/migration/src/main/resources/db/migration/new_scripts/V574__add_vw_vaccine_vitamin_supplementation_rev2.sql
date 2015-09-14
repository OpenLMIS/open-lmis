DROP VIEW IF EXISTS vw_vaccine_vitamin_supplementation;
CREATE OR REPLACE VIEW vw_vaccine_vitamin_supplementation AS 
WITH tmp AS (
SELECT 
geographic_zones.id AS geographic_zone_id,
geographic_zones.name AS geographic_zone_name,
geographic_zones.levelid AS level_id,
geographic_zones.parentid AS parent_id,
facilities.id AS facility_id,
facilities.code AS facility_code,
facilities.name AS facility_name,
vaccine_reports.periodid AS period_id,
processing_periods.name AS period_name,
processing_periods.startdate::date AS period_start_date,
processing_periods.enddate::date AS period_end_date,
date_part('year'::text, processing_periods.startdate) AS report_year,
vaccine_vitamin_supplementation_age_groups.name AS age_group,
vaccine_vitamins.name AS vitamin_name,
vaccine_reports.id AS report_id,
vaccine_reports.status,
vaccine_report_vitamin_supplementation_line_items.malevalue AS male_value,
vaccine_report_vitamin_supplementation_line_items.femalevalue AS female_value,
vaccine_report_vitamin_supplementation_line_items.malevalue + vaccine_report_vitamin_supplementation_line_items.femalevalue AS total_value
FROM vaccine_report_vitamin_supplementation_line_items
JOIN vaccine_reports ON vaccine_report_vitamin_supplementation_line_items.reportid = vaccine_reports.id
JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
JOIN facilities ON vaccine_reports.facilityid = facilities.id
JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
JOIN vaccine_vitamin_supplementation_age_groups ON vaccine_vitamin_supplementation_age_groups.id = vaccine_report_vitamin_supplementation_line_items.vitaminagegroupid
JOIN vaccine_vitamins ON vaccine_vitamins.id = vaccine_report_vitamin_supplementation_line_items.vaccinevitaminid
)
SELECT t.geographic_zone_id,
t.geographic_zone_name,
t.level_id,
t.parent_id,
t.facility_id,
t.facility_code,
t.facility_name,
t.report_id,
t.period_id,
t.report_year,
t.age_group,
t.vitamin_name,
t.period_start_date,
t.male_value,
t.female_value,
t.total_value
FROM tmp t;