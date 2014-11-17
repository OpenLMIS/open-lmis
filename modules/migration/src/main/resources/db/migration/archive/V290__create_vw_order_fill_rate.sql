
DROP VIEW IF EXISTS vw_order_fill_rate;

  CREATE OR REPLACE VIEW vw_order_fill_rate AS
 WITH order_summary AS (
         SELECT dw_orders.programid, dw_orders.periodid, dw_orders.scheduleid, dw_orders.facilitytypeid, dw_orders.productid, dw_orders.productcategoryid, dw_orders.requisitiongroupid, dw_orders.facilityid, products.primaryname, products.code AS productcode, facilities.name AS facilityname, sum(COALESCE(dw_orders.quantityapproved, 0)::numeric) AS quantityapproved, sum(COALESCE(dw_orders.quantityreceived, 0)::numeric) AS quantityreceived, sum(
                CASE
                    WHEN COALESCE(dw_orders.quantityapproved, 0) = 0 THEN 0::numeric
                    ELSE
                    CASE
                        WHEN dw_orders.quantityapproved > 0 THEN 1::numeric
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
                END) AS totalproductsreceived
           FROM dw_orders
      JOIN products ON products.id = dw_orders.productid
   JOIN facilities ON facilities.id = dw_orders.facilityid
   JOIN requisition_group_program_schedules rps ON rps.requisitiongroupid = dw_orders.requisitiongroupid AND rps.programid = dw_orders.programid
  WHERE dw_orders.status::text = ANY (ARRAY['APPROVED'::character varying::text, 'RELEASED'::character varying::text])
  GROUP BY dw_orders.programid, dw_orders.periodid, dw_orders.requisitiongroupid, dw_orders.facilityid, dw_orders.productid, products.primaryname, products.code, facilities.name, dw_orders.scheduleid, dw_orders.facilitytypeid, dw_orders.productcategoryid
        )
 SELECT order_summary.programid, order_summary.periodid, order_summary.requisitiongroupid, order_summary.facilityid, order_summary.productid, order_summary.primaryname, order_summary.quantityapproved, order_summary.quantityreceived, order_summary.scheduleid, order_summary.facilitytypeid, order_summary.productcategoryid, order_summary.productcode, order_summary.facilityname,
        CASE
            WHEN COALESCE(order_summary.quantityapproved, 0::numeric) = 0::numeric THEN 0::numeric
            ELSE order_summary.quantityreceived / order_summary.quantityapproved * 100::numeric
        END AS item_fill_rate, order_summary.totalproductsapproved, order_summary.totalproductsreceived
   FROM order_summary;

