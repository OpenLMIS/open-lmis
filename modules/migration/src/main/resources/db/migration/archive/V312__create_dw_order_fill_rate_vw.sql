-- View: dw_order_fill_rate_vw

DROP VIEW IF EXISTS dw_order_fill_rate_vw;

CREATE OR REPLACE VIEW dw_order_fill_rate_vw AS 
 WITH order_summary AS (
          SELECT dw_orders.programid, dw_orders.periodid, 
             dw_orders.geographiczoneid, dw_orders.facilityid, 
             sum(
                 CASE
                     WHEN COALESCE(dw_orders.quantityapproved, 0) = 0 THEN 0::numeric
                     ELSE 
                     CASE
                         WHEN dw_orders.quantityapproved > 0 THEN 1::numeric
                         ELSE 0::numeric
                     END
                 END) AS totalproductsapproved, 
             sum(
                 CASE
                     WHEN COALESCE(dw_orders.quantityreceived, 0) = 0 THEN 0::numeric
                     ELSE 
                     CASE
                         WHEN dw_orders.quantityreceived > 0 THEN 1::numeric
                         ELSE 0::numeric
                     END
                 END) AS totalproductsreceived
            FROM dw_orders
           WHERE dw_orders.status::text = ANY (ARRAY['APPROVED'::character varying::text, 'RELEASED'::character varying::text])
           GROUP BY dw_orders.programid, dw_orders.periodid, dw_orders.geographiczoneid, dw_orders.facilityid
         )
  SELECT order_summary.programid, order_summary.periodid, 
     order_summary.geographiczoneid, order_summary.facilityid, 
     order_summary.totalproductsapproved, order_summary.totalproductsreceived, 
         CASE
             WHEN COALESCE(order_summary.totalproductsapproved, 0::numeric) = 0::numeric THEN 0::numeric
             ELSE order_summary.totalproductsreceived / order_summary.totalproductsapproved * 100::numeric
         END AS order_fill_rate
   FROM order_summary;

ALTER TABLE dw_order_fill_rate_vw
  OWNER TO postgres;
