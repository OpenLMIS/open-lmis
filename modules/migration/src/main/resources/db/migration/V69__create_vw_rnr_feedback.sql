-- View: vw_rnr_feedback
/*

2013-09-09 Muhammad Ahmed - created

*/
DROP VIEW IF EXISTS vw_rnr_feedback;

CREATE OR REPLACE VIEW vw_rnr_feedback AS 
 SELECT vw_requisition_detail.program_id, vw_requisition_detail.program_name, 
    vw_requisition_detail.product_id, vw_requisition_detail.product_code, 
    vw_requisition_detail.product_primaryname, 
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
    vw_requisition_detail.beginningbalance, 
    vw_requisition_detail.quantityreceived, 
    vw_requisition_detail.quantitydispensed, vw_requisition_detail.stockinhand, 
    vw_requisition_detail.quantityrequested, 
    vw_requisition_detail.calculatedorderquantity, 
    vw_requisition_detail.quantityapproved, 
    vw_requisition_detail.totallossesandadjustments, 
    vw_requisition_detail.newpatientcount, vw_requisition_detail.stockoutdays, 
    vw_requisition_detail.normalizedconsumption, vw_requisition_detail.amc, 
    vw_requisition_detail.maxmonthsofstock, 
    vw_requisition_detail.maxstockquantity, vw_requisition_detail.packstoship, 
    vw_requisition_detail.packsize, vw_requisition_detail.fullsupply, 
    vw_requisition_detail.nominalmaxmonth, vw_requisition_detail.nominaleop, 
    vw_requisition_detail.dispensingunit, shipment_line_items.quantityshipped,
        CASE
            WHEN vw_requisition_detail.previousstockinhandavailable THEN 
            CASE
                WHEN fn_previous_cb(vw_requisition_detail.req_id, vw_requisition_detail.product_code) <> vw_requisition_detail.beginningbalance THEN 1
                ELSE 0
            END
            ELSE 0
        END AS err_open_balance, 
        CASE
            WHEN vw_requisition_detail.calculatedorderquantity <> vw_requisition_detail.quantityrequested THEN 0
            ELSE 1
        END AS err_qty_required,
        CASE
            WHEN vw_requisition_detail.quantityreceived <> shipment_line_items.quantityshipped THEN 0
            ELSE 1
        END AS err_qty_received, 
        CASE
            WHEN vw_requisition_detail.stockinhand <> (COALESCE(vw_requisition_detail.beginningbalance, 0) + COALESCE(vw_requisition_detail.quantityreceived, 0) - COALESCE(vw_requisition_detail.quantitydispensed, 0) + COALESCE(vw_requisition_detail.totallossesandadjustments, 0)) THEN 1
            ELSE 0
        END AS err_qty_stockinhand
   FROM vw_requisition_detail
   LEFT JOIN Orders ON Orders.rnrId =  vw_requisition_detail.req_id
   LEFT JOIN shipment_line_items ON Orders.id = shipment_line_items.orderId;

ALTER TABLE vw_rnr_feedback
  OWNER TO postgres;

