-- View: vw_e2e_stock_status

DROP VIEW IF EXISTS vw_e2e_stock_status_fill_rates;
DROP VIEW IF EXISTS vw_e2e_stock_status;
CREATE OR REPLACE VIEW vw_e2e_stock_status AS 
 WITH tmp AS (
          select date_part('year'::text, periodstartdate ) AS reportyear,
                 date_part('month'::text, periodstartdate) AS reportmonth,
                 d.processingperiodname AS reportperiodname,
                 d.createddate::date reporteddate,
                 extract (year from d.createddate) reportedyear,
                 extract (month from d.createddate) reportedmonth,
                 d.programcode, d.programname, d.facilityname,
                 d.geographiczonename as district, d.productcode, d.productprimaryname product,
                 d.amc, d.soh stockinhand, d.mos, d.stocking stockstatus, d.reporting reportingstatus, d.stockoutdays,
                 d.stockedoutinpast, d.suppliedinpast,
                 d.programid, d.periodid, d.facilityid, d.productid

from dw_orders d )
 SELECT t.reportyear,
    t.reportmonth,
    (t.reportmonth / 4::double precision)::integer + 1 AS reportquarter,
    t.reportperiodname,
    t.reporteddate,
    t.reportedyear,
    t.reportedmonth,
    t.programcode,
    t.programname,
    t.facilityname,
    t.district,
    t.productcode,
    t.product,
    t.stockinhand,
    t.amc,
    t.mos,
    t.stockstatus,
    t.reportingstatus,
    t.stockoutdays,
    t.stockedoutinpast,
    t.suppliedinpast,
    t.programid,
    t.periodid,
    t.facilityid,
    t.productid
   FROM tmp t;
ALTER TABLE vw_e2e_stock_status
  OWNER TO openlmis;
