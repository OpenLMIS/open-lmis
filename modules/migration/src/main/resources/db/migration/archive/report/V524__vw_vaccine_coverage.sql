-- View: vw_vaccine_coverage

DROP VIEW IF EXISTS vw_vaccine_coverage;

CREATE OR REPLACE VIEW vw_vaccine_coverage AS 
 SELECT a.geographic_zone_id,
    a.geographic_zone_name,
    a.level_id,
    a.parent_id,
    a.period_id,
    a.period_name,
    a.period_start_date,
    a.period_end_date,
    a.facility_id,
    a.facility_code,
    a.facility_name,
    a.report_id,
    a.product_id,
    a.product_code,
    a.product_name,
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
    0 AS within_outside_total,
    0 AS within_outside_coverage,
    0 AS cum_within_total,
    0 AS cum_within_coverage,
    0 AS cum_outside_total,
    0 AS cum_outside_coverage,
    0 AS cum_within_outside_total,
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
                    products.id AS product_id,
                    products.code AS product_code,
                    products.primaryname AS product_name,
                    vaccine_report_coverage_line_items.displayorder AS display_order,
                    vaccine_report_coverage_line_items.displayname AS display_name,
                    vaccine_report_coverage_line_items.regularmale AS within_male,
                    vaccine_report_coverage_line_items.regularfemale AS within_female,
                    vaccine_report_coverage_line_items.outreachmale AS outside_male,
                    vaccine_report_coverage_line_items.outreachfemale AS outside_female,
                    vaccine_report_coverage_line_items.campaignmale AS camp_male,
                    vaccine_report_coverage_line_items.campaignfemale AS camp_female
                   FROM vaccine_report_coverage_line_items
                     JOIN vaccine_reports ON vaccine_report_coverage_line_items.reportid = vaccine_reports.id
                     JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
                     JOIN facilities ON vaccine_reports.facilityid = facilities.id
                     JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
                     JOIN products ON vaccine_report_coverage_line_items.productid = products.id
                )
         SELECT t.geographic_zone_id,
            t.geographic_zone_name,
            t.level_id,
            t.parent_id,
            t.period_id,
            t.period_name,
            t.period_start_date,
            t.period_end_date,
            t.facility_id,
            t.facility_code,
            t.facility_name,
            t.report_id,
            t.product_id,
            t.product_code,
            t.product_name,
            t.display_order,
            t.display_name,
            t.within_male,
            t.within_female,
            t.within_male + t.within_female AS within_total,
            t.outside_male,
            t.outside_female,
            t.outside_male + t.outside_female AS outside_total,
            t.camp_male,
            t.camp_female,
            t.camp_male + t.camp_female AS camp_total,
            t.within_male + t.within_female + t.outside_male + t.outside_female AS within_outside_total
           FROM temp t) a;

ALTER TABLE vw_vaccine_coverage
  OWNER TO postgres;
