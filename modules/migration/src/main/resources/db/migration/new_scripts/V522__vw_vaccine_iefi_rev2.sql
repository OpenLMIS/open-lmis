-- View: vw_vaccine_iefi

DROP VIEW IF EXISTS vw_vaccine_iefi;

CREATE OR REPLACE VIEW vw_vaccine_iefi AS 
 SELECT processing_periods.id AS period_id,
    processing_periods.name AS period_name,
    vaccine_reports.id AS report_id,
    vaccine_reports.facilityid AS facility_id,
    vaccine_reports.status,
    products.id AS product_id,
    products.code AS product_code,
    products.primaryname AS product_name,
    vaccine_report_adverse_effect_line_items.date AS aefi_date,
    vaccine_report_adverse_effect_line_items.batch AS aefi_batch,
    vaccine_report_adverse_effect_line_items.expiry AS aefi_expiry_date,
    vaccine_report_adverse_effect_line_items.cases AS aefi_case,
    vaccine_report_adverse_effect_line_items.notes AS aefi_notes,
    vaccine_report_adverse_effect_line_items.isinvestigated AS is_investigated,
    vaccine_report_adverse_effect_line_items.manufacturer
   FROM vaccine_report_adverse_effect_line_items
     JOIN vaccine_reports ON vaccine_report_adverse_effect_line_items.reportid = vaccine_reports.id
     JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
     JOIN products ON vaccine_report_adverse_effect_line_items.productid = products.id;

ALTER TABLE vw_vaccine_iefi
  OWNER TO postgres;
