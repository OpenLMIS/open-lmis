-- View: vw_vaccine_disease_surveillance

DROP VIEW IF EXISTS vw_vaccine_disease_surveillance;

CREATE OR REPLACE VIEW vw_vaccine_disease_surveillance AS 
 WITH tmp AS (
         SELECT geographic_zones.id AS geographic_zone_id,
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
            vaccine_reports.id AS report_id,
            vaccine_reports.status,
            vaccine_report_disease_line_items.diseaseid AS disease_id,
            vaccine_report_disease_line_items.diseasename AS disease_name,
            vaccine_report_disease_line_items.displayorder AS display_order,
            vaccine_report_disease_line_items.cases,
            vaccine_report_disease_line_items.death,
            ( SELECT sum(COALESCE(l.cases, 0)) AS sum
                   FROM vaccine_report_disease_line_items l
                     JOIN vaccine_reports r ON r.id = l.reportid
                     JOIN processing_periods pp ON pp.id = r.periodid
                  WHERE date_part('year'::text, pp.startdate) = date_part('year'::text, processing_periods.startdate) 
                  AND pp.startdate <= processing_periods.startdate AND r.facilityid = vaccine_reports.facilityid 
                  AND l.diseaseid = vaccine_report_disease_line_items.diseaseid) AS cum_cases,
            ( SELECT sum(COALESCE(l.death, 0)) AS sum
                   FROM vaccine_report_disease_line_items l
                     JOIN vaccine_reports r ON r.id = l.reportid
                     JOIN processing_periods pp ON pp.id = r.periodid
                  WHERE date_part('year'::text, pp.startdate) = date_part('year'::text, processing_periods.startdate) 
                  AND pp.startdate <= processing_periods.startdate AND r.facilityid = vaccine_reports.facilityid 
                  AND l.diseaseid = vaccine_report_disease_line_items.diseaseid) AS cum_deaths
           FROM vaccine_report_disease_line_items
             JOIN vaccine_reports ON vaccine_report_disease_line_items.reportid = vaccine_reports.id
             JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
             JOIN facilities ON vaccine_reports.facilityid = facilities.id
             JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
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
    t.period_start_date,
    t.cases,
    t.death,
    t.disease_name,
    t.cum_cases,
    t.cum_deaths
   FROM tmp t
  ORDER BY t.facility_id, t.period_start_date;

