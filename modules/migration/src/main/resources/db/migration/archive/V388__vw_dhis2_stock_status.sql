-- View: vw_dhis2_stock_status

DROP VIEW IF EXISTS vw_dhis2_stock_status;

CREATE OR REPLACE VIEW vw_dhis2_stock_status AS 
 SELECT date_part('year'::text, ( SELECT processing_periods_1.startdate
           FROM processing_periods processing_periods_1
          WHERE processing_periods_1.id = requisitions.periodid
         LIMIT 1)) AS report_year,
    date_part('month'::text, ( SELECT processing_periods_1.startdate
           FROM processing_periods processing_periods_1
          WHERE processing_periods_1.id = requisitions.periodid
         LIMIT 1)) AS report_month,
    processing_periods.name AS report_period_name,
    (( SELECT requisition_status_changes.createddate
           FROM requisition_status_changes
          WHERE requisition_status_changes.rnrid = requisitions.id AND requisition_status_changes.status::text = 'SUBMITTED'::text
         LIMIT 1))::date AS reported_date,
    programs.name AS program_name,
    facilities.name AS facility_name,
    geographic_zones.name AS district,
    requisition_line_items.productcode AS product_code,
    requisition_line_items.product,
    requisition_line_items.stockinhand,
    requisition_line_items.amc,
        CASE
            WHEN COALESCE(requisition_line_items.amc, 0) = 0 THEN null::numeric
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
     JOIN dhis2_products ON dhis2_products.elmiscode::text = requisition_line_items.productcode::text
  WHERE requisitions.status::text <> ALL (ARRAY['SKIPPED'::character varying, 'INITIATED'::character varying]::text[]);

ALTER TABLE vw_dhis2_stock_status
  OWNER TO openlmis;
