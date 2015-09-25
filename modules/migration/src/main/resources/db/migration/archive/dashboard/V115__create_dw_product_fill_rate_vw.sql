DROP VIEW IF EXISTS dw_product_fill_rate_vw;

CREATE OR REPLACE VIEW dw_product_fill_rate_vw AS 
 SELECT dw_orders.programid, dw_orders.geographiczoneid, dw_orders.periodid, dw_orders.productid, 
    products.primaryname, 
    sum(
        CASE
            WHEN COALESCE(dw_orders.quantityapproved, 0) = 0 THEN 0::numeric
            ELSE 
            CASE
                WHEN dw_orders.status::text = 'APPROVED'::text THEN dw_orders.quantityapproved::numeric
                ELSE 0::numeric
            END
        END) AS quantityapproved, 
    sum(
        CASE
            WHEN COALESCE(dw_orders.quantityshipped, 0) = 0 THEN 0::numeric
            ELSE 
            CASE
                WHEN dw_orders.status::text = 'APPROVED'::text THEN dw_orders.quantityshipped::numeric
                ELSE 0::numeric
            END
        END) AS quantityshipped, 
    sum(
        CASE
            WHEN COALESCE(dw_orders.quantityreceived, 0) = 0 THEN 0::numeric
            ELSE 
            CASE
                WHEN dw_orders.status::text = 'APPROVED'::text THEN dw_orders.quantityreceived::numeric
                ELSE 0::numeric
            END
        END) AS quantityreceived, 
 
case when 
sum(
        CASE
            WHEN COALESCE(dw_orders.quantityapproved, 0) = 0 THEN 0::numeric
            ELSE 
            CASE
                WHEN dw_orders.status::text = 'APPROVED'::text THEN dw_orders.quantityapproved::numeric
                ELSE 0::numeric
            END
        END) = 0 then 0 else
   sum(
        CASE
            WHEN COALESCE(dw_orders.quantityshipped, 0) = 0 THEN 0::numeric
            ELSE 
            CASE
                WHEN dw_orders.status::text = 'APPROVED'::text THEN dw_orders.quantityshipped::numeric
                ELSE 0::numeric
            END
        END) / sum(
        CASE
            WHEN COALESCE(dw_orders.quantityapproved, 0) = 0 THEN 0::numeric
            ELSE 
            CASE
                WHEN dw_orders.status::text = 'APPROVED'::text THEN dw_orders.quantityapproved::numeric
                ELSE 0::numeric
            END
        END) end * 100::numeric AS order_fill_rate_shipped, 
   
case when 
sum(
        CASE
            WHEN COALESCE(dw_orders.quantityapproved, 0) = 0 THEN 0::numeric
            ELSE 
            CASE
                WHEN dw_orders.status::text = 'APPROVED'::text THEN dw_orders.quantityapproved::numeric
                ELSE 0::numeric
            END
        END) = 0 then 0 else
 sum(
        CASE
            WHEN COALESCE(dw_orders.quantityreceived, 0) = 0 THEN 0::numeric
            ELSE 
            CASE
                WHEN dw_orders.status::text = 'APPROVED'::text THEN dw_orders.quantityreceived::numeric
                ELSE 0::numeric
            END
        END) / sum(
        CASE
            WHEN COALESCE(dw_orders.quantityapproved, 0) = 0 THEN 0::numeric
            ELSE 
            CASE
                WHEN dw_orders.status::text = 'APPROVED'::text THEN dw_orders.quantityapproved::numeric
                ELSE 0::numeric
            END
        END) end * 100::numeric AS order_fill_rate_received
   FROM dw_orders
   JOIN products ON products.id = dw_orders.productid
  WHERE dw_orders.status::text = ANY (ARRAY['APPROVED'::character varying::text, 'RELEASED'::character varying::text])
  GROUP BY dw_orders.programid, dw_orders.geographiczoneid, dw_orders.periodid, dw_orders.productid, products.primaryname;

ALTER TABLE dw_product_fill_rate_vw
  OWNER TO postgres;
COMMENT ON VIEW dw_product_fill_rate_vw
  IS 'dw_product_fill_rate_vw- 
calculate product fill rate- Total qty received / Total qty approved
Filters: Geographic zone id (district), periodid, productid, productcode
created Feb 13, 2014 mahmed 
';


