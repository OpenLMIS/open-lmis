DROP VIEW IF EXISTS vw_vaccine_disease_surveillance;
CREATE OR REPLACE VIEW vw_vaccine_disease_surveillance AS 
 WITH tmp AS (
         SELECT vaccine_reports.facilityid AS facility_id,
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
            vaccine_report_disease_line_items.death
           FROM vaccine_report_disease_line_items
             JOIN vaccine_reports ON vaccine_report_disease_line_items.reportid = vaccine_reports.id
             JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
        )
 SELECT t.facility_id,
    t.report_id,
    t.period_id,
    t.report_year,
    t.period_start_date,
    t.cases,
    t.death,
    sum(t.cases) OVER (PARTITION BY t.facility_id, t.period_start_date ORDER BY t.period_start_date, t.facility_id) AS cum_cases
   FROM tmp t
  ORDER BY t.facility_id, t.period_start_date;

ALTER TABLE vw_vaccine_disease_surveillance
  OWNER TO postgres;
