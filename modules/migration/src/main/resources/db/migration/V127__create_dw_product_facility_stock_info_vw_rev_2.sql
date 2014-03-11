DROP VIEW IF EXISTS dw_product_facility_stock_info_vw;

CREATE OR REPLACE VIEW dw_product_facility_stock_info_vw AS 
 SELECT dw_orders.programid, dw_orders.geographiczoneid, dw_orders.periodid,
    dw_orders.productid, products.primaryname,dw_orders.facilityid,dw_orders.facilityname,
    SUM(dw_orders.amc) AS AMC,
    SUM(dw_orders.soh) AS SOH,
    SUM(dw_orders.mos) AS MOS,
    stocking
    ,count(stocking) over (partition by geographiczoneid,programid,periodid, productid,stocking) AS stockingStat

   FROM dw_orders
   JOIN products ON products.id = dw_orders.productid
   GROUP BY dw_orders.geographiczoneid,dw_orders.programid,dw_orders.periodid, dw_orders.productid, products.primaryname,dw_orders.facilityid,stocking,dw_orders.facilityname
  ORDER BY dw_orders.geographiczoneid,dw_orders.programid,dw_orders.periodid, dw_orders.productid,stocking,facilityid;

ALTER TABLE dw_product_facility_stock_info_vw
  OWNER TO postgres;