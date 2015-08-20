-- View: vw_e2e_pipeline

DROP VIEW IF EXISTS vw_e2e_pipeline;

CREATE OR REPLACE VIEW vw_e2e_pipeline AS 
 WITH tmp AS (
         SELECT processing_periods.startdate::date AS startdate, 
            processing_periods.enddate::date AS enddate,
            processing_periods.id AS periodid, processing_periods.numberofmonths,
            programs.id AS programid,
            programs.code AS programcode,
            programs.name AS programname,
            requisition_line_items.productcode,
            requisition_line_items.product,
            requisition_line_items.dispensingunit,
            requisition_line_items.quantitydispensed AS dispensed,
            requisition_line_items.totallossesandadjustments AS adjustment
           FROM facilities
             JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
             JOIN requisitions ON requisitions.facilityid = facilities.id
             JOIN processing_periods ON requisitions.periodid = processing_periods.id
             JOIN requisition_line_items ON requisition_line_items.rnrid = requisitions.id
             JOIN programs ON requisitions.programid = programs.id
          WHERE requisitions.status::text <> ALL (ARRAY['SKIPPED'::character varying::text, 'INITIATED'::character varying::text, 'SUBMITTED'::character varying::text])
          limit 2000
)
 SELECT date_part('year'::text, t.startdate)::integer AS reportyear,
    date_part('month'::text, t.startdate)::integer AS reportmonth,
    (date_part('month'::text, t.startdate) / 4::double precision)::integer + 1 AS reportquarter,
    t.startdate,
    t.enddate,
    t.periodid,
    t.numberofmonths,
    t.programid,
    t.programcode,
    t.programname,
    t.productcode,
    t.product,
    t.dispensingunit,
    t.dispensed,
    t.adjustment
   FROM tmp t;

ALTER TABLE vw_e2e_pipeline
  OWNER TO openlmis;
