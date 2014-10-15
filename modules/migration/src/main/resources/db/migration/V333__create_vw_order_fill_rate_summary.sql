
DROP VIEW IF EXISTS  vw_order_fill_rate_summary;

CREATE OR REPLACE VIEW vw_order_fill_rate_summary AS
 WITH query AS (
         SELECT dw_orders.status, dw_orders.facilityid, dw_orders.periodid, dw_orders.productprimaryname AS product, products.code AS productcode, facilities.name AS facilityname, dw_orders.scheduleid, dw_orders.facilitytypeid, dw_orders.productid, dw_orders.productcategoryid, dw_orders.programid, dw_orders.geographiczoneid AS zoneid, dw_orders.geographiczonename AS zonename, sum(COALESCE(fn_previous_period(dw_orders.programid, dw_orders.facilityid, dw_orders.periodid, dw_orders.productcode), 0)::numeric) AS quantityapproved, sum(COALESCE(dw_orders.quantityreceived, 0)::numeric) AS quantityreceived, sum(
                CASE
                    WHEN COALESCE(fn_previous_period(dw_orders.programid, dw_orders.facilityid, dw_orders.periodid, dw_orders.productcode), 0) = 0 THEN 0::numeric
                    ELSE
                    CASE
                        WHEN fn_previous_period(dw_orders.programid, dw_orders.facilityid, dw_orders.periodid, dw_orders.productcode) > 0 THEN 1::numeric
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
                    WHEN COALESCE(dw_orders.quantityreceived, 0) > 1 AND COALESCE(fn_previous_period(dw_orders.programid, dw_orders.facilityid, dw_orders.periodid, dw_orders.productcode), 0) = 0 THEN 1::numeric
                    ELSE 0::numeric
                END) AS totalproductspushed
           FROM dw_orders
      JOIN products ON products.id = dw_orders.productid AND products.primaryname::text = dw_orders.productprimaryname::text
   JOIN facilities ON facilities.id = dw_orders.facilityid
  WHERE dw_orders.status::text = ANY (ARRAY['RELEASED'::character varying::text])
  GROUP BY dw_orders.scheduleid, dw_orders.facilitytypeid, dw_orders.productid, dw_orders.status, dw_orders.facilityid, dw_orders.periodid, dw_orders.productprimaryname, products.code, facilities.name, dw_orders.productcategoryid, dw_orders.programid, dw_orders.geographiczoneid, dw_orders.geographiczonename
        )
 SELECT query.status, query.facilityid, query.periodid, query.product, query.productcode, query.facilityname, query.scheduleid, query.facilitytypeid, query.productid, query.productcategoryid, query.programid, query.zoneid, query.zonename, query.quantityapproved, query.quantityreceived, query.totalproductsapproved, query.totalproductsreceived, query.totalproductspushed,
        CASE
            WHEN COALESCE(query.quantityapproved, 0::numeric) = 0::numeric THEN 0::numeric
            ELSE round(COALESCE(query.quantityreceived * 100::numeric / query.quantityapproved, 1::numeric), 0)
        END AS item_fill_rate
   FROM query
  GROUP BY query.status, query.facilityid, query.periodid, query.product, query.productcode, query.facilityname, query.scheduleid, query.facilitytypeid, query.productid, query.productcategoryid, query.programid, query.zoneid, query.zonename, query.quantityapproved, query.totalproductspushed, query.quantityreceived, query.totalproductsapproved, query.totalproductsreceived;

ALTER TABLE vw_order_fill_rate_summary
  OWNER TO postgres;

