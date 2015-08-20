-- View: vw_e2e_stock_status

DROP VIEW IF EXISTS vw_e2e_stock_status;

CREATE OR REPLACE VIEW vw_e2e_stock_status AS 
 WITH tmp AS (
         SELECT date_part('year'::text, ( SELECT processing_periods_1.startdate
                   FROM processing_periods processing_periods_1
                  WHERE processing_periods_1.id = requisitions.periodid
                 LIMIT 1)) AS reportyear, 
            date_part('month'::text, ( SELECT processing_periods_1.startdate
                   FROM processing_periods processing_periods_1
                  WHERE processing_periods_1.id = requisitions.periodid
                 LIMIT 1)) AS reportmonth, 
            processing_periods.name AS reportperiodname, 
            (( SELECT requisition_status_changes.createddate
                   FROM requisition_status_changes
                  WHERE requisition_status_changes.rnrid = requisitions.id AND requisition_status_changes.status::text = 'SUBMITTED'::text
                 LIMIT 1))::date AS reporteddate, 
            programs.code AS programcode, programs.name AS programname, 
            facilities.name AS facilityname, 
            geographic_zones.name AS district, 
            requisition_line_items.productcode, 
            requisition_line_items.product, 
            requisition_line_items.stockinhand AS stockInHand, 
            requisition_line_items.amc, 
                CASE
                    WHEN COALESCE(requisition_line_items.amc, 0) = 0 THEN NULL::numeric
                    ELSE round((requisition_line_items.stockinhand::double precision / requisition_line_items.amc::double precision)::numeric, 2)
                END AS mos, 
            date_part('year'::text, ( SELECT requisition_status_changes.createddate
                   FROM requisition_status_changes
                  WHERE requisition_status_changes.rnrid = requisitions.id AND requisition_status_changes.status::text = 'SUBMITTED'::text
                 LIMIT 1))::integer AS reportedyear, 
            date_part('month'::text, ( SELECT requisition_status_changes.createddate
                   FROM requisition_status_changes
                  WHERE requisition_status_changes.rnrid = requisitions.id AND requisition_status_changes.status::text = 'SUBMITTED'::text
                 LIMIT 1))::integer AS reportedmonth
           FROM facilities
      JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
   JOIN requisitions ON requisitions.facilityid = facilities.id
   JOIN processing_periods ON requisitions.periodid = processing_periods.id
   JOIN requisition_line_items ON requisition_line_items.rnrid = requisitions.id
   JOIN programs ON requisitions.programid = programs.id
  WHERE requisitions.status::text <> ALL (ARRAY['SKIPPED'::character varying::text, 'INITIATED'::character varying::text, 'SUBMITTED'::character varying::text])
        )
 SELECT t.reportyear, t.reportmonth, 
    (t.reportmonth / 4::double precision)::integer + 1 AS reportquarter, 
    t.reportperiodname, t.reporteddate, t.reportedyear, t.reportedmonth, 
    t.programcode, t.programname, t.facilityname, t.district, 
    t.productcode, t.product, t.stockInHand, t.amc, t.mos
   FROM tmp t;
ALTER TABLE vw_e2e_stock_status
  OWNER TO openlmis;
