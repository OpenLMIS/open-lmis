 DROP VIEW IF EXISTS vw_order_fill_rate;

CREATE OR REPLACE VIEW vw_order_fill_rate AS 
 SELECT dw_orders.status, dw_orders.facilityid, dw_orders.periodid, dw_orders.productfullname AS product, products.code AS productcode, facilities.name AS facilityname, dw_orders.scheduleid, dw_orders.facilitytypeid, dw_orders.productid, dw_orders.productcategoryid, dw_orders.programid, dw_orders.geographiczoneid AS zoneid, dw_orders.geographiczonename AS zonename, sum(COALESCE(dw_orders.quantityapprovedprev, 0)::numeric) AS quantityapproved, sum(COALESCE(dw_orders.quantityreceived, 0)::numeric) AS quantityreceived, sum(
        CASE
            WHEN COALESCE(dw_orders.quantityapprovedprev, 0) = 0 THEN 0::numeric
            ELSE 
            CASE
                WHEN dw_orders.quantityapprovedprev > 0 THEN 1::numeric
                ELSE 0::numeric
            END
        END) AS totalproductsapproved, sum(
        CASE
            WHEN COALESCE(dw_orders.quantityreceived, 0) = 0 THEN 0::numeric
            ELSE 
            CASE
                WHEN dw_orders.quantityreceived > 0 THEN 1::numeric
                ELSE 0::numeric
            END
        END) AS totalproductsreceived, sum(
        CASE
            WHEN COALESCE(dw_orders.quantityreceived, 0) > 1 AND COALESCE(dw_orders.quantityapprovedprev, 0) = 0 THEN 1::numeric
            ELSE 0::numeric
        END) AS totalproductspushed
   FROM dw_orders
   JOIN products ON products.id = dw_orders.productid AND products.primaryname::text = dw_orders.productprimaryname::text
   JOIN facilities ON facilities.id = dw_orders.facilityid
  WHERE dw_orders.status::text = ANY (ARRAY['RELEASED'::character varying::text])
  GROUP BY dw_orders.scheduleid, dw_orders.facilitytypeid, dw_orders.productid, dw_orders.status, dw_orders.facilityid, dw_orders.periodid, dw_orders.productfullname, products.code, facilities.name, dw_orders.productcategoryid, dw_orders.programid, dw_orders.geographiczoneid, dw_orders.geographiczonename;

ALTER TABLE vw_order_fill_rate
  OWNER TO postgres;

