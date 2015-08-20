DROP VIEW IF EXISTS vw_e2e_stock_status_fill_rates;
CREATE OR REPLACE VIEW vw_e2e_stock_status_fill_rates AS 
 SELECT vw_e2e_stock_status.reportyear,
    vw_e2e_stock_status.reportmonth,
    vw_e2e_stock_status.reportquarter,
    vw_e2e_stock_status.reportperiodname,
    vw_e2e_stock_status.reporteddate,
    vw_e2e_stock_status.reportedyear,
    vw_e2e_stock_status.reportedmonth,
    vw_e2e_stock_status.programcode,
    vw_e2e_stock_status.programname,
    vw_e2e_stock_status.facilityname,
    vw_e2e_stock_status.district,
    vw_e2e_stock_status.productcode,
    vw_e2e_stock_status.product,
    vw_e2e_stock_status.stockinhand,
    vw_e2e_stock_status.amc,
    vw_e2e_stock_status.mos,
    vw_e2e_stock_status.stockstatus,
    vw_e2e_stock_status.reportingstatus,
    vw_e2e_stock_status.stockoutdays,
    vw_e2e_stock_status.stockedoutinpast,
    vw_e2e_stock_status.suppliedinpast,
    vw_e2e_stock_status.programid,
    vw_e2e_stock_status.periodid,
    vw_e2e_stock_status.facilityid,
    vw_e2e_stock_status.productid,
    dw_order_fill_rate_vw.order_fill_rate AS orderfillrate,
    dw_product_fill_rate_vw.order_fill_rate AS productfillrate
   FROM vw_e2e_stock_status
     LEFT JOIN dw_order_fill_rate_vw ON dw_order_fill_rate_vw.programid = vw_e2e_stock_status.programid AND dw_order_fill_rate_vw.periodid = vw_e2e_stock_status.periodid AND dw_order_fill_rate_vw.facilityid = vw_e2e_stock_status.facilityid
     LEFT JOIN dw_product_fill_rate_vw ON dw_product_fill_rate_vw.programid = vw_e2e_stock_status.programid AND dw_product_fill_rate_vw.periodid = vw_e2e_stock_status.periodid AND dw_product_fill_rate_vw.facilityid = vw_e2e_stock_status.facilityid AND dw_product_fill_rate_vw.productid = vw_e2e_stock_status.productid;

ALTER TABLE vw_e2e_stock_status_fill_rates
  OWNER TO openlmis;
