-- View: dw_order_fill_rate_vw

DROP VIEW IF EXISTS dw_order_fill_rate_vw;

CREATE OR REPLACE VIEW dw_order_fill_rate_vw AS 
 WITH order_summary AS (
         SELECT dw_orders.programid,
            dw_orders.periodid,
            dw_orders.requisitiongroupid,
            dw_orders.facilityid,
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
      JOIN requisition_group_program_schedules rps ON rps.requisitiongroupid = dw_orders.requisitiongroupid AND rps.programid = dw_orders.programid
     WHERE dw_orders.status::text = ANY (ARRAY['APPROVED'::character varying::text, 'RELEASED'::character varying::text])
     GROUP BY dw_orders.programid, dw_orders.periodid, dw_orders.requisitiongroupid, dw_orders.facilityid
        )
 SELECT order_summary.programid,
    order_summary.periodid,
    order_summary.requisitiongroupid,
    order_summary.facilityid,
    order_summary.totalproductsapproved,
    order_summary.totalproductsreceived,
        CASE
            WHEN COALESCE(order_summary.totalproductsapproved, 0::numeric) = 0::numeric THEN 0::numeric
            ELSE order_summary.totalproductsreceived / order_summary.totalproductsapproved * 100::numeric
        END AS order_fill_rate
   FROM order_summary;

ALTER TABLE dw_order_fill_rate_vw
  OWNER TO postgres;
