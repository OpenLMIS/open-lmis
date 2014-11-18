
DROP VIEW IF EXISTS  vw_order_fill_rate CASCADE;

CREATE OR REPLACE VIEW vw_order_fill_rate AS

 SELECT vw_order_fill_rate_details.programid,

    vw_order_fill_rate_details.program, vw_order_fill_rate_details.category,
    vw_order_fill_rate_details.categoryid, vw_order_fill_rate_details.periodid,
    vw_order_fill_rate_details.period, vw_order_fill_rate_details.scheduleid,
    vw_order_fill_rate_details.schedule,
    vw_order_fill_rate_details.facilitytypeid,
    vw_order_fill_rate_details.facilitytype, vw_order_fill_rate_details.total,
    vw_order_fill_rate_details.rgroupid, vw_order_fill_rate_details.rgroup,
    vw_order_fill_rate_details.facilityid, vw_order_fill_rate_details.facility,
    vw_order_fill_rate_details.productcode, vw_order_fill_rate_details.product,
    vw_order_fill_rate_details.productid,
    vw_order_fill_rate_details.supplyingfacility,
    vw_order_fill_rate_details.zoneid, vw_order_fill_rate_details.region,
    vw_order_fill_rate_details.req_id,
    COALESCE(vw_order_fill_rate_details.receipts, 0) AS receipts,
    COALESCE(vw_order_fill_rate_details.approved, 0) AS approved,
        CASE
            WHEN fn_previous_pd(vw_order_fill_rate_details.req_id, vw_order_fill_rate_details.periodid, vw_order_fill_rate_details.productcode) <> COALESCE(vw_order_fill_rate_details.receipts, 0) THEN 1
            ELSE 0
        END AS err_qty_received
   FROM vw_order_fill_rate_details;
ALTER TABLE vw_order_fill_rate
  OWNER TO postgres;
