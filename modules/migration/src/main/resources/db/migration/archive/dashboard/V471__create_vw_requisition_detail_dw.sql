-- add program code 
DROP VIEW IF EXISTS vw_requisition_detail_2;
DROP VIEW IF EXISTS vw_requisition_detail_dw;

CREATE OR REPLACE VIEW vw_requisition_detail_dw AS 
 SELECT programs.id AS program_id,
    programs.code AS program_code,
    programs.name AS program_name,
    processing_schedules.id AS processing_schedule_id,
    processing_schedules.name AS processing_schedule_name,
    processing_periods.id AS period_id,
    processing_periods.name AS period_name,
    processing_periods.startdate AS period_start_date,
    processing_periods.enddate AS period_end_date,
    geographic_zones.id AS geographic_zone_id,
    geographic_zones.name AS geographic_zone_name,
    geographic_zones.levelid AS geographic_zone_level,
    facility_types.id AS facility_type_id,
    facility_types.name AS facility_type_name,
    facility_types.nominaleop AS facility_type_nominaleop,
    facility_types.nominalmaxmonth AS facility_type_nominalmaxmonth,
    facilities.id AS facility_id,
    facilities.code AS facility_code,
    facilities.name AS facility_name,
    facilities.sdp AS facility_sdp,
    facilities.enabled AS facility_enabled,
    facility_approved_products.maxmonthsofstock AS facility_approved_product_maxmonthsofstock,
    facility_approved_products.minmonthsofstock AS facility_approved_product_minmonthsofstock,
    facility_approved_products.eop AS facility_approved_product_eop,
    requisitions.id AS requisition_id,
    requisitions.status AS requisition_status,
    requisitions.emergency AS requisition_emergency,
    product_categories.id AS product_category_id,
    product_categories.name AS product_category_name,
    products.productgroupid AS product_group_id,
    products.id AS product_id,
    products.code AS product_code,
    products.description AS product_description,
    products.dispensingunit AS product_dispensingunit,
    products.primaryname AS product_primaryname,
    products.fullname AS product_fullname,
    products.tracer AS product_tracer,
    requisition_line_items.amc,
    requisition_line_items.beginningbalance,
    requisition_line_items.calculatedorderquantity,
    requisition_line_items.createddate,
    requisition_line_items.fullsupply,
    requisition_line_items.id AS line_item_id,
    requisition_line_items.maxmonthsofstock,
    requisition_line_items.maxstockquantity,
    requisition_line_items.modifieddate,
    requisition_line_items.newpatientcount,
    requisition_line_items.normalizedconsumption,
    requisition_line_items.packsize,
    requisition_line_items.packstoship,
    requisition_line_items.previousstockinhand,
    requisition_line_items.quantityapproved,
    requisition_line_items.quantitydispensed,
    requisition_line_items.quantityreceived,
    requisition_line_items.quantityrequested,
    requisition_line_items.skipped,
    requisition_line_items.stockinhand,
    requisition_line_items.stockoutdays,
    requisition_line_items.totallossesandadjustments,
    shipment_line_items.quantityordered,
    shipment_line_items.quantityshipped,
    orders.modifieddate AS ordereddate,
    shipment_line_items.shippeddate
   FROM requisition_line_items
     JOIN requisitions ON requisition_line_items.rnrid = requisitions.id
     JOIN products ON requisition_line_items.productcode::text = products.code::text
     JOIN programs ON requisitions.programid = programs.id
     JOIN facilities ON requisitions.facilityid = facilities.id
     JOIN processing_periods ON requisitions.periodid = processing_periods.id
     LEFT JOIN orders ON requisitions.id = orders.id
     LEFT JOIN shipment_line_items ON shipment_line_items.orderid = orders.id
     JOIN processing_schedules ON processing_periods.scheduleid = processing_schedules.id
     JOIN program_products ON products.id = program_products.productid AND program_products.programid = programs.id
     JOIN product_categories ON program_products.productcategoryid = product_categories.id
     JOIN facility_types ON facilities.typeid = facility_types.id
     JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
     LEFT JOIN facility_approved_products ON facility_approved_products.facilitytypeid = facility_types.id AND facility_approved_products.programproductid = program_products.id;

ALTER TABLE vw_requisition_detail_dw
  OWNER TO postgres;