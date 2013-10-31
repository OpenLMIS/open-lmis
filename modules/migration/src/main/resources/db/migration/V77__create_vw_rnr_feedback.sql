-- View: vw_rnr_feedback
/*

2013-10-30 - Muhammad Ahmed - modified function call to previous period balance
2013-10-27 Muhammad Ahmed  - add shipped total column
2013-10-25 Muhammad Ahmed - modified join between orders and requisitions
2013-09-09 Muhammad Ahmed - created

*/
DROP VIEW IF EXISTS vw_rnr_feedback;

CREATE OR REPLACE VIEW vw_rnr_feedback AS
 SELECT vw_requisition_detail.program_id, vw_requisition_detail.program_name,
    vw_requisition_detail.product_id, vw_requisition_detail.product_code,
    vw_requisition_detail.product_primaryname,
    shipment_line_items.substitutedproductcode,
    shipment_line_items.substitutedproductname,
    vw_requisition_detail.product_description,
    vw_requisition_detail.indicator_product,
    vw_requisition_detail.processing_periods_id,
    vw_requisition_detail.processing_periods_name,
    vw_requisition_detail.processing_periods_start_date,
    vw_requisition_detail.processing_periods_end_date,
    vw_requisition_detail.processing_schedules_id,
    vw_requisition_detail.facility_type_id,
    vw_requisition_detail.facility_type_name,
    vw_requisition_detail.facility_code, vw_requisition_detail.facility_name,
    vw_requisition_detail.productcode, vw_requisition_detail.product,
    vw_requisition_detail.facility_id, vw_requisition_detail.req_id,
    vw_requisition_detail.req_status, vw_requisition_detail.req_line_id,
    vw_requisition_detail.zone_id, vw_requisition_detail.region,
    vw_requisition_detail.du_code, vw_requisition_detail.pf_code,
    COALESCE(vw_requisition_detail.beginningbalance, 0) AS beginningbalance,
    COALESCE(vw_requisition_detail.quantityreceived, 0) AS quantityreceived,
    COALESCE(vw_requisition_detail.quantitydispensed, 0) AS quantitydispensed,
    COALESCE(vw_requisition_detail.stockinhand, 0) AS stockinhand,
    COALESCE(vw_requisition_detail.quantityapproved, 0) AS quantityapproved,
    COALESCE(vw_requisition_detail.totallossesandadjustments, 0) AS totallossesandadjustments,
    COALESCE(vw_requisition_detail.newpatientcount, 0) AS newpatientcount,
    COALESCE(vw_requisition_detail.stockoutdays, 0) AS stockoutdays,
    COALESCE(vw_requisition_detail.normalizedconsumption, 0) AS normalizedconsumption,
    COALESCE(vw_requisition_detail.amc, 0) AS amc,
    COALESCE(vw_requisition_detail.maxmonthsofstock, 0) AS maxmonthsofstock,
    COALESCE(vw_requisition_detail.maxstockquantity, 0) AS maxstockquantity,
    COALESCE(vw_requisition_detail.packstoship, 0) AS packstoship,
    vw_requisition_detail.packsize, vw_requisition_detail.fullsupply,
    vw_requisition_detail.nominalmaxmonth, vw_requisition_detail.nominaleop,
    vw_requisition_detail.dispensingunit,
    COALESCE(vw_requisition_detail.calculatedorderquantity, 0) AS calculatedorderquantity,
    COALESCE(vw_requisition_detail.quantityrequested, 0) AS quantityrequested,
    COALESCE(shipment_line_items.quantityshipped, 0) AS quantityshipped,
    COALESCE(shipment_line_items.substitutedproductquantityshipped, 0) AS substitutedproductquantityshipped,
    COALESCE(shipment_line_items.quantityshipped, 0) + COALESCE(shipment_line_items.substitutedproductquantityshipped, 0) AS quantity_shipped_total,
        CASE
            WHEN fn_previous_cb(vw_requisition_detail.req_id, vw_requisition_detail.product_code) <> COALESCE(vw_requisition_detail.beginningbalance, 0) THEN 1
            ELSE 0
        END AS err_open_balance,
        CASE
            WHEN COALESCE(vw_requisition_detail.calculatedorderquantity, 0) <> COALESCE(vw_requisition_detail.quantityrequested, 0) THEN 1
            ELSE 0
        END AS err_qty_required,
        CASE
            WHEN COALESCE(vw_requisition_detail.quantityreceived, 0) <> (COALESCE(shipment_line_items.quantityshipped, 0) + COALESCE(shipment_line_items.substitutedproductquantityshipped, 0)) THEN 1
            ELSE 0
        END AS err_qty_received,
        CASE
            WHEN COALESCE(vw_requisition_detail.stockinhand, 0) <> (COALESCE(vw_requisition_detail.beginningbalance, 0) + COALESCE(vw_requisition_detail.quantityreceived, 0) - COALESCE(vw_requisition_detail.quantitydispensed, 0) + COALESCE(vw_requisition_detail.totallossesandadjustments, 0)) THEN 1
            ELSE 0
        END AS err_qty_stockinhand
   FROM vw_requisition_detail
   LEFT JOIN orders ON orders.id = vw_requisition_detail.req_id
   LEFT JOIN shipment_line_items ON orders.id = shipment_line_items.orderid AND vw_requisition_detail.product_code::text = shipment_line_items.productcode::text;

ALTER TABLE vw_rnr_feedback
  OWNER TO postgres;

