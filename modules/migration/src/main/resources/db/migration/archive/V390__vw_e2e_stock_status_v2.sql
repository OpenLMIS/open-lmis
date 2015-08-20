-- View: vw_e2e_stock_status

DROP VIEW IF EXISTS vw_e2e_stock_status;

CREATE OR REPLACE VIEW vw_e2e_stock_status AS 
  WITH tmp AS (
          SELECT date_part('year'::text, ( SELECT processing_periods.startdate
                    FROM processing_periods processing_periods
                   WHERE (processing_periods.id = requisitions.periodid)
                  LIMIT 1)) AS reportYear,
             date_part('month'::text, ( SELECT processing_periods.startdate
                    FROM processing_periods processing_periods
                   WHERE (processing_periods.id = requisitions.periodid)
                  LIMIT 1)) AS reportMonth,
             processing_periods.name AS reportPeriodName,
             (( SELECT requisition_status_changes.createddate
                    FROM requisition_status_changes
                   WHERE ((requisition_status_changes.rnrid = requisitions.id) AND ((requisition_status_changes.status)::text = 'SUBMITTED'::text))
                  LIMIT 1))::date AS reportedDate,
             programs.code AS programCode,
             programs.name AS programName,
             facilities.name AS facilityName,
             geographic_zones.name AS geographiczoneName,
             requisition_line_items.productcode AS productCode,
             requisition_line_items.product AS productName,
             requisition_line_items.stockinhand AS soh,
             requisition_line_items.amc,
                 CASE
                     WHEN (COALESCE(requisition_line_items.amc, 0) = 0) THEN NULL::numeric
                     ELSE round((((requisition_line_items.stockinhand)::double precision / (requisition_line_items.amc)::double precision))::numeric, 2)
                 END AS mos,
             (date_part('year'::text, ( SELECT requisition_status_changes.createddate
                    FROM requisition_status_changes
                   WHERE ((requisition_status_changes.rnrid = requisitions.id) AND ((requisition_status_changes.status)::text = 'SUBMITTED'::text))
                  LIMIT 1)))::integer AS reportedYear,
             (date_part('month'::text, ( SELECT requisition_status_changes.createddate
                    FROM requisition_status_changes
                   WHERE ((requisition_status_changes.rnrid = requisitions.id) AND ((requisition_status_changes.status)::text = 'SUBMITTED'::text))
                  LIMIT 1)))::integer AS reportedMonth
            FROM (((((facilities
              JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)))
              JOIN requisitions ON ((requisitions.facilityid = facilities.id)))
              JOIN processing_periods ON ((requisitions.periodid = processing_periods.id)))
              JOIN requisition_line_items ON ((requisition_line_items.rnrid = requisitions.id)))
              JOIN programs ON ((requisitions.programid = programs.id)))
           WHERE ((requisitions.status)::text <> ALL (ARRAY[('SKIPPED'::character varying)::text, ('INITIATED'::character varying)::text, ('SUBMITTED'::character varying)::text]))
         )
  SELECT t.reportYear,
     t.reportMonth,
     (((t.reportMonth / (4)::double precision))::integer + 1) AS reportQuarter,
     t.reportPeriodName,
     t.reportedDate,
     t.reportedYear,
     t.reportedMonth,
     t.programCode,
     t.programName,
     t.facilityName,
     t.geographicZoneName,
     t.productCode,
     t.productName,
     t.soh,
     t.amc,
     t.mos
   FROM tmp t;

ALTER TABLE vw_e2e_stock_status
  OWNER TO openlmis;
