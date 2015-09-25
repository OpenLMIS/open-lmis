DROP VIEW IF EXISTS vw_vaccine_coverage;
CREATE OR REPLACE VIEW vw_vaccine_coverage AS 
SELECT 
a.program_id,
a.geographic_zone_id,
a.geographic_zone_name,
a.level_id,
a.parent_id,
a.period_id,
a.period_name,
a.period_year,
a.period_start_date,
a.period_end_date,
a.facility_id,
a.facility_code,
a.facility_name,
a.report_id,
a.product_id,
a.product_code,
a.product_name,
a.dose_id,
a.display_order,
a.display_name,
a.within_male,
a.within_female,
a.within_total,
0 AS within_coverage,
a.outside_male,
a.outside_female,
a.outside_total,
0 AS outside_coverage,
a.camp_male,
a.camp_female,
a.camp_total,
a.within_outside_total,
fn_get_vaccine_coverage_denominator(program_id,facility_id,period_year,product_id,dose_id) denominator,
cum_within_total,
cum_outside_total,
(cum_within_total + cum_outside_total) cum_within_outside_total,
0 AS within_outside_coverage,
0 AS cum_within_coverage,
0 AS cum_outside_coverage,
0 AS cum_within_outside_coverage
FROM ( WITH temp AS (
SELECT geographic_zones.id AS geographic_zone_id,
geographic_zones.name AS geographic_zone_name,
geographic_zones.levelid AS level_id,
geographic_zones.parentid AS parent_id,
processing_periods.id AS period_id,
processing_periods.name AS period_name,
processing_periods.startdate AS period_start_date,
processing_periods.enddate AS period_end_date,
facilities.id AS facility_id,
facilities.code AS facility_code,
facilities.name AS facility_name,
vaccine_reports.id AS report_id,
vaccine_reports.programid AS program_id,
products.id AS product_id,
products.code AS product_code,
products.primaryname AS product_name,
vaccine_report_coverage_line_items.doseid AS dose_id,
vaccine_report_coverage_line_items.displayorder AS display_order,
vaccine_report_coverage_line_items.displayname AS display_name,
vaccine_report_coverage_line_items.regularmale AS within_male,
vaccine_report_coverage_line_items.regularfemale AS within_female,
vaccine_report_coverage_line_items.outreachmale AS outside_male,
vaccine_report_coverage_line_items.outreachfemale AS outside_female,
vaccine_report_coverage_line_items.campaignmale AS camp_male,
vaccine_report_coverage_line_items.campaignfemale AS camp_female,
( SELECT sum(COALESCE(l.regularmale, 0) + COALESCE(l.regularfemale, 0) ) AS sum
                   FROM vaccine_report_coverage_line_items l
                     JOIN vaccine_reports r ON r.id = l.reportid
                     JOIN processing_periods pp ON pp.id = r.periodid
                  WHERE date_part('year'::text, pp.startdate) = date_part('year'::text, processing_periods.startdate) 
                  AND pp.startdate <= processing_periods.startdate 
                  AND r.facilityid = vaccine_reports.facilityid 
                  AND l.productid = vaccine_report_coverage_line_items.productid
                  AND l.doseid = vaccine_report_coverage_line_items.doseid) AS cum_within_total,
( SELECT sum(COALESCE(l.outreachmale, 0) + COALESCE(l.outreachfemale, 0) ) AS sum
                   FROM vaccine_report_coverage_line_items l
                     JOIN vaccine_reports r ON r.id = l.reportid
                     JOIN processing_periods pp ON pp.id = r.periodid
                  WHERE date_part('year'::text, pp.startdate) = date_part('year'::text, processing_periods.startdate) 
                  AND pp.startdate <= processing_periods.startdate 
                  AND r.facilityid = vaccine_reports.facilityid 
                  AND l.productid = vaccine_report_coverage_line_items.productid
                  AND l.doseid = vaccine_report_coverage_line_items.doseid) AS cum_outside_total
FROM vaccine_report_coverage_line_items
JOIN vaccine_reports ON vaccine_report_coverage_line_items.reportid = vaccine_reports.id
JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
JOIN facilities ON vaccine_reports.facilityid = facilities.id
JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
JOIN products ON vaccine_report_coverage_line_items.productid = products.id
)
SELECT 
b.program_id,
b.geographic_zone_id,
b.geographic_zone_name,
b.level_id,
b.parent_id,
b.period_id,
b.period_name,
date_part('year'::text, b.period_start_date)::integer AS period_year,
b.period_start_date,
b.period_end_date,
b.facility_id,
b.facility_code,
b.facility_name,
b.report_id,
b.product_id,
b.product_code,
b.product_name,
b.dose_id,
b.display_order,
b.display_name,
COALESCE(b.within_male, 0) AS within_male,
COALESCE(b.within_female, 0) AS within_female,
COALESCE(b.within_male, 0) + COALESCE(b.within_female, 0) AS within_total,
COALESCE(b.outside_male, 0) AS outside_male,
COALESCE(b.outside_female, 0) AS outside_female,
COALESCE(b.outside_male, 0) + COALESCE(b.outside_female, 0) AS outside_total,
COALESCE(b.camp_male, 0) AS camp_male,
COALESCE(b.camp_female, 0) AS camp_female,
COALESCE(b.camp_male, 0) + COALESCE(b.camp_female, 0) AS camp_total,
COALESCE(b.within_male, 0) + COALESCE(b.within_female, 0) + COALESCE(b.outside_male, 0) + COALESCE(b.outside_female, 0) AS within_outside_total,
b.cum_within_total,
b.cum_outside_total
FROM temp b) a;