DROP VIEW IF EXISTS vw_requisition_adjustment;

CREATE OR REPLACE VIEW "vw_requisition_adjustment" AS
 SELECT
    programs.id               AS program_id,
    programs.name             AS program_name,
    processing_periods.id     AS processing_periods_id,
    processing_periods.name   AS processing_periods_name,
    processing_periods.startdate AS processing_periods_start_date,
    processing_periods.enddate AS processing_periods_end_date,
    processing_schedules.id   AS processing_schedules_id,
    processing_schedules.name AS processing_schedules_name,
    facility_types.name       AS facility_type_name,
    facility_types.id         AS facility_type_id,
    facilities.code           AS facility_code,
    facilities.id             AS facility_id,
    facilities.name           AS facility_name,
    requisition_line_items.id AS requisition_line_item_id,
    requisition_line_items.productcode,
    requisition_line_items.product,
    products.id               AS product_id,
    product_categories.name   AS product_category_name,
    product_categories.id     AS product_category_id,
    requisitions.status       AS req_status,
    requisition_line_items.beginningbalance,
    requisition_line_items.quantityreceived,
    requisition_line_items.quantitydispensed,
    requisition_line_items.stockinhand,
    requisition_line_items.quantityrequested,
    requisition_line_items.calculatedorderquantity,
    requisition_line_items.quantityapproved,
    requisition_line_items.totallossesandadjustments,
    requisition_line_items.newpatientcount,
    requisition_line_items.stockoutdays,
    requisition_line_items.normalizedconsumption,
    requisition_line_items.amc,
    requisition_line_items.maxmonthsofstock,
    requisition_line_items.maxstockquantity,
    requisition_line_items.packstoship,
    requisition_line_items.packsize,
    requisition_line_items.fullsupply,
    requisition_line_item_losses_adjustments.type AS adjustment_type,
    requisition_line_item_losses_adjustments.quantity AS adjutment_qty,
    losses_adjustments_types.displayorder AS adjustment_display_order,
    losses_adjustments_types.additive AS adjustment_additive,
    fn_get_supplying_facility_name(requisitions.supervisorynodeid)::text AS supplying_facility_name,
    requisition_line_items.id
   FROM requisition_line_items
         JOIN requisitions                  ON requisition_line_items.rnrid = requisitions.id
         JOIN products                      ON (requisition_line_items.productcode)::text = (products.code)::text
         JOIN programs                      ON requisitions.programid = programs.id
         JOIN program_products              ON (products.id = program_products.productid) AND (program_products.programid = programs.id)
         JOIN processing_periods            ON requisitions.periodid = processing_periods.id
         JOIN product_categories            ON program_products.productcategoryid = product_categories.id
         JOIN processing_schedules          ON processing_periods.scheduleid = processing_schedules.id
         JOIN facilities                    ON requisitions.facilityid = facilities.id
         JOIN facility_types                ON facilities.typeid = facility_types.id
         JOIN geographic_zones              ON facilities.geographiczoneid = geographic_zones.id
         JOIN requisition_line_item_losses_adjustments ON requisition_line_items.id = requisition_line_item_losses_adjustments.requisitionlineitemid
         JOIN losses_adjustments_types      ON (requisition_line_item_losses_adjustments.type)::text = (losses_adjustments_types.name)::text;
