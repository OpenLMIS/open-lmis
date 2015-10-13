DROP VIEW if exists dw_product_fill_rate_vw;

CREATE OR REPLACE VIEW dw_product_fill_rate_vw AS 
 SELECT dw_orders.programid,
    dw_orders.periodid,
    dw_orders.geographiczoneid,
    dw_orders.facilityid,
    dw_orders.productid,
    products.primaryname,
    sum(COALESCE(dw_orders.quantityapproved, 0)::numeric) AS quantityapproved,
    sum(
        CASE
            WHEN COALESCE(dw_orders.quantityreceived, 0) = 0 THEN dw_orders.quantityshipped::numeric
            ELSE COALESCE(dw_orders.quantityreceived, 0)::numeric
        END) AS quantityreceived,
        CASE
            WHEN COALESCE(sum(COALESCE(dw_orders.quantityapproved, 0)::numeric), 0::numeric) = 0::numeric THEN 0::numeric
            ELSE round( (sum(
            CASE
                WHEN COALESCE(dw_orders.quantityreceived, 0) = 0 THEN dw_orders.quantityshipped::numeric
                ELSE COALESCE(dw_orders.quantityreceived, 0)::numeric
            END) / sum(COALESCE(dw_orders.quantityapproved, 0)::numeric) * 100),2) 
        END AS order_fill_rate 
   FROM dw_orders
     JOIN products ON products.id = dw_orders.productid
  WHERE dw_orders.status::text = ANY (ARRAY['APPROVED'::character varying::text, 'RELEASED'::character varying::text])
  GROUP BY dw_orders.programid, dw_orders.periodid, dw_orders.geographiczoneid, dw_orders.facilityid, dw_orders.productid, products.primaryname;

ALTER TABLE dw_product_fill_rate_vw
  OWNER TO postgres;
