-- View: vw_e2e_stock_status

DROP VIEW IF EXISTS vw_e2e_stock_status;

CREATE OR REPLACE VIEW vw_e2e_stock_status AS 
 WITH tmp AS (
         SELECT date_part('year'::text, ( SELECT processing_periods.startdate
                   FROM processing_periods processing_periods
                  WHERE processing_periods.id = requisitions.periodid
                 LIMIT 1)) AS report_year,
            date_part('month'::text, ( SELECT processing_periods.startdate
                   FROM processing_periods processing_periods
                  WHERE processing_periods.id = requisitions.periodid
                 LIMIT 1)) AS report_month,
            processing_periods.name AS report_period_name,
            (( SELECT requisition_status_changes.createddate
                   FROM requisition_status_changes
                  WHERE requisition_status_changes.rnrid = requisitions.id AND requisition_status_changes.status::text = 'SUBMITTED'::text
                 LIMIT 1))::date AS reported_date,
            programs.code AS program_code,
            programs.name AS program_name,
            facilities.name AS facility_name,
            geographic_zones.name AS district,
            requisition_line_items.productcode AS product_code,
            requisition_line_items.product,
            requisition_line_items.stockinhand,
            requisition_line_items.amc,
                CASE
                    WHEN COALESCE(requisition_line_items.amc, 0) = 0 THEN NULL::numeric
                    ELSE round((requisition_line_items.stockinhand::double precision / requisition_line_items.amc::double precision)::numeric, 2)
                END AS mos,
            date_part('year'::text, ( SELECT requisition_status_changes.createddate
                   FROM requisition_status_changes
                  WHERE requisition_status_changes.rnrid = requisitions.id AND requisition_status_changes.status::text = 'SUBMITTED'::text
                 LIMIT 1))::integer AS reported_year,
            date_part('month'::text, ( SELECT requisition_status_changes.createddate
                   FROM requisition_status_changes
                  WHERE requisition_status_changes.rnrid = requisitions.id AND requisition_status_changes.status::text = 'SUBMITTED'::text
                 LIMIT 1))::integer AS reported_month
           FROM facilities
             JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
             JOIN requisitions ON requisitions.facilityid = facilities.id
             JOIN processing_periods ON requisitions.periodid = processing_periods.id
             JOIN requisition_line_items ON requisition_line_items.rnrid = requisitions.id
             JOIN programs ON requisitions.programid = programs.id
          WHERE requisitions.status::text <> ALL (ARRAY['SKIPPED'::character varying::text, 'INITIATED'::character varying::text, 'SUBMITTED'::character varying::text])
        )
 SELECT t.report_year,
    t.report_month,
    (t.report_month / 4::double precision)::integer + 1 AS report_quarter,
    t.report_period_name,
    t.reported_date,
    t.program_code,
    t.program_name,
    t.facility_name,
    t.district,
    t.product_code,
    t.product,
    t.stockinhand,
    t.amc,
    t.mos,
    t.reported_year,
    t.reported_month
   FROM tmp t;

ALTER TABLE vw_e2e_stock_status
  OWNER TO openlmis;
