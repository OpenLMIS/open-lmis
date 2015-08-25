-- View: vw_vaccine_stock_status

DROP VIEW IF EXISTS vw_vaccine_stock_status;

CREATE OR REPLACE VIEW vw_vaccine_stock_status AS 
 SELECT geographic_zones.id AS geographic_zone_id,
    geographic_zones.name AS geographic_zone_name,
    geographic_zones.levelid AS level_id,
    geographic_zones.parentid AS parent_id,
    facilities.id AS facility_id,
    facilities.code AS facility_code,
    facilities.name AS facility_name,
    processing_periods.name AS period_name,
    processing_periods.startdate AS period_start_date,
    processing_periods.enddate AS period_end_date,
    processing_periods.id AS period_id,
    vaccine_reports.id AS report_id,
    product_categories.code AS product_category_code,
    vaccine_report_logistics_line_items.productid AS product_id,
    vaccine_report_logistics_line_items.productcode AS product_code,
    vaccine_report_logistics_line_items.productname AS product_name,
    vaccine_report_logistics_line_items.displayorder AS display_order,
    vaccine_report_logistics_line_items.openingbalance AS opening_balanace,
    vaccine_report_logistics_line_items.quantityreceived AS quantity_received,
    vaccine_report_logistics_line_items.quantityissued AS quantity_issued,
    vaccine_report_logistics_line_items.quantityvvmalerted AS quantity_vvm_alerted,
    vaccine_report_logistics_line_items.quantityfreezed AS quantity_freezed,
    vaccine_report_logistics_line_items.quantityexpired AS quantity_expired,
    vaccine_report_logistics_line_items.quantitydiscardedunopened AS quantity_discarded_unopened,
    vaccine_report_logistics_line_items.quantitydiscardedopened AS quantity_discarded_opened,
    vaccine_report_logistics_line_items.quantitywastedother AS quantity_wasted_other,
    vaccine_report_logistics_line_items.daysstockedout AS days_stocked_out,
    vaccine_report_logistics_line_items.closingbalance AS closing_balance,
    vaccine_discarding_reasons.name AS reason_for_discarding,
    vaccine_report_campaign_line_items.childrenvaccinated AS children_immunized,
    vaccine_report_campaign_line_items.pregnantwomanvaccinated AS pregnant_women_immunized,
    vaccine_report_campaign_line_items.childrenvaccinated / (vaccine_report_logistics_line_items.quantityissued + vaccine_report_logistics_line_items.quantitydiscardedunopened) * 100 AS usage_rate,
    100 - 0 / (vaccine_report_logistics_line_items.quantityissued + vaccine_report_logistics_line_items.quantitydiscardedunopened) * 100 AS wastage_rate
   FROM vaccine_report_logistics_line_items
     JOIN vaccine_reports ON vaccine_report_logistics_line_items.reportid = vaccine_reports.id
     JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
     JOIN vaccine_discarding_reasons ON vaccine_report_logistics_line_items.discardingreasonid = vaccine_discarding_reasons.id
     JOIN facilities ON vaccine_reports.facilityid = facilities.id
     JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
     JOIN vaccine_report_campaign_line_items ON vaccine_report_campaign_line_items.reportid = vaccine_reports.id
     JOIN program_products ON program_products.programid = vaccine_reports.programid AND program_products.productid = vaccine_report_logistics_line_items.productid
     JOIN product_categories ON program_products.productcategoryid = product_categories.id;

ALTER TABLE vw_vaccine_stock_status
  OWNER TO postgres;
