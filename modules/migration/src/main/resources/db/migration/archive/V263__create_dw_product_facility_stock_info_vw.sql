--  View: dw_product_facility_stock_info_vw

DROP VIEW IF EXISTS dw_product_facility_stock_info_vw;

CREATE OR REPLACE VIEW dw_product_facility_stock_info_vw AS 
 SELECT 
    dw_orders.requisitiongroupid,
    dw_orders.geographiczoneid,
    dw_orders.programid,
    dw_orders.periodid,
    dw_orders.productid,
    products.primaryname,
    dw_orders.facilityid,
    dw_orders.facilityname,
    dw_orders.amc,
    dw_orders.soh,
    dw_orders.mos,
    dw_orders.stocking
   FROM dw_orders
   JOIN products ON products.id = dw_orders.productid
   JOIN requisition_group_members rgm ON dw_orders.facilityid = rgm.facilityid AND dw_orders.requisitiongroupid = rgm.requisitiongroupid
  ORDER BY dw_orders.requisitiongroupid, dw_orders.programid, dw_orders.periodid, dw_orders.productid, products.primaryname, dw_orders.stocking;

ALTER TABLE dw_product_facility_stock_info_vw
  OWNER TO postgres;
