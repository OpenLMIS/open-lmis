-- View: vw_vaccine_stock_status

 DROP VIEW vw_vaccine_stock_status;

CREATE OR REPLACE VIEW vw_vaccine_stock_status AS 
 WITH t AS (
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
            vaccine_reports.programid AS program_id,
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
            COALESCE(vaccine_report_logistics_line_items.quantityissued, 0) + COALESCE(vaccine_report_logistics_line_items.quantitydiscardedunopened, 0) AS usage_denominator,
            cv.vaccinated
           FROM vaccine_report_logistics_line_items
             JOIN vaccine_reports ON vaccine_report_logistics_line_items.reportid = vaccine_reports.id
             JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
             LEFT JOIN vaccine_discarding_reasons ON vaccine_report_logistics_line_items.discardingreasonid = vaccine_discarding_reasons.id
             JOIN facilities ON vaccine_reports.facilityid = facilities.id
             JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
             JOIN program_products ON program_products.programid = vaccine_reports.programid AND program_products.productid = vaccine_report_logistics_line_items.productid
             JOIN product_categories ON program_products.productcategoryid = product_categories.id
             LEFT JOIN ( SELECT vaccine_report_coverage_line_items.reportid,
                    vaccine_report_coverage_line_items.productid,
                    sum(COALESCE(vaccine_report_coverage_line_items.regularmale, 0) + COALESCE(vaccine_report_coverage_line_items.regularfemale, 0) + COALESCE(vaccine_report_coverage_line_items.outreachmale, 0) + COALESCE(vaccine_report_coverage_line_items.outreachfemale, 0) + COALESCE(vaccine_report_coverage_line_items.campaignmale, 0) + COALESCE(vaccine_report_coverage_line_items.campaignfemale, 0)) AS vaccinated
                   FROM vaccine_report_coverage_line_items
                  GROUP BY vaccine_report_coverage_line_items.reportid, vaccine_report_coverage_line_items.productid) cv ON cv.reportid = vaccine_reports.id AND vaccine_report_logistics_line_items.productid = cv.productid
        )
 SELECT t.geographic_zone_id,
    t.geographic_zone_name,
    t.level_id,
    t.parent_id,
    t.facility_id,
    t.facility_code,
    t.facility_name,
    t.period_name,
    t.period_start_date,
    t.period_end_date,
    t.period_id,
    t.report_id,
    t.program_id,
    t.product_category_code,
    t.product_id,
    t.product_code,
    t.product_name,
    t.display_order,
    t.opening_balanace,
    t.quantity_received,
    t.quantity_issued,
    t.quantity_vvm_alerted,
    t.quantity_freezed,
    t.quantity_expired,
    t.quantity_discarded_unopened,
    t.quantity_discarded_opened,
    t.quantity_wasted_other,
    t.days_stocked_out,
    t.closing_balance,
    t.reason_for_discarding,
        CASE
            WHEN t.reason_for_discarding::text = 'Expired'::text THEN t.quantity_discarded_unopened
            ELSE 0
        END AS expired,
        CASE
            WHEN t.reason_for_discarding::text = 'Broken'::text THEN t.quantity_discarded_unopened
            ELSE 0
        END AS broken,
        CASE
            WHEN t.reason_for_discarding::text = 'Cold Chain Failure'::text THEN t.quantity_discarded_unopened
            ELSE 0
        END AS cold_chain_failure,
        CASE
            WHEN t.reason_for_discarding::text <> ALL (ARRAY['Expired'::character varying::text, 'Broken'::character varying::text, 'Cold Chain Failure'::character varying::text]) THEN t.quantity_discarded_unopened
            ELSE 0
        END AS other,
    0 AS children_immunized,
    0 AS pregnant_women_immunized,
    t.vaccinated,
    t.usage_denominator,
        CASE
            WHEN t.usage_denominator > 0 THEN round(t.vaccinated::numeric / t.usage_denominator::numeric, 4) * 100::numeric
            ELSE NULL::numeric
        END AS usage_rate,
        CASE
            WHEN t.usage_denominator > 0 THEN 100::numeric - round(t.vaccinated::numeric / t.usage_denominator::numeric, 4) * 100::numeric
            ELSE NULL::numeric
        END AS wastage_rate
   FROM t order by t.display_order;

ALTER TABLE vw_vaccine_stock_status
  OWNER TO postgres;
