/*

2013-09-09 - mahmed - add previousstockinhandavailable
2013-09-05 - recreated to new name - older versions of this wiew will be removed

??? Muhammad Ahmed - created

*/
DROP VIEW IF EXISTS vw_requisition_detail CASCADE;


CREATE OR REPLACE VIEW vw_requisition_detail AS 
 SELECT programs.id AS program_id, programs.name AS program_name, 
    products.id AS product_id, products.code AS product_code, 
    products.primaryname AS product_primaryname, 
    products.description AS product_description, 
    products.tracer AS indicator_product, 
    processing_periods.id AS processing_periods_id, 
    processing_periods.name AS processing_periods_name, 
    processing_periods.startdate AS processing_periods_start_date, 
    processing_periods.enddate AS processing_periods_end_date, 
    processing_periods.scheduleid AS processing_schedules_id, 
    facility_types.id AS facility_type_id, 
    facility_types.name AS facility_type_name, facilities.code AS facility_code, 
    facilities.name AS facility_name, requisition_line_items.productcode, 
    requisition_line_items.product, requisition_line_items.beginningbalance, 
    requisition_line_items.quantityreceived, 
    requisition_line_items.quantitydispensed, 
    requisition_line_items.stockinhand, 
    requisition_line_items.quantityrequested, 
    requisition_line_items.calculatedorderquantity, 
    requisition_line_items.quantityapproved, 
    requisition_line_items.totallossesandadjustments, 
    requisition_line_items.newpatientcount, requisition_line_items.stockoutdays, 
    requisition_line_items.normalizedconsumption, requisition_line_items.amc, 
    requisition_line_items.maxmonthsofstock, 
    requisition_line_items.maxstockquantity, requisition_line_items.packstoship, 
    requisition_line_items.packsize, requisition_line_items.fullsupply, 
    requisition_line_items.previousstockinhandavailable, 
    facilities.id AS facility_id, requisitions.id AS req_id, 
    requisitions.status AS req_status, requisition_line_items.id AS req_line_id, 
    geographic_zones.id AS zone_id, geographic_zones.name AS region, 
    facility_types.nominalmaxmonth, facility_types.nominaleop, 
    dosage_units.code AS du_code, product_forms.code AS pf_code, 
    products.dispensingunit
   FROM requisition_line_items
   JOIN requisitions ON requisitions.id = requisition_line_items.rnrid
   JOIN facilities ON facilities.id = requisitions.facilityid
   JOIN facility_types ON facility_types.id = facilities.typeid
   JOIN processing_periods ON processing_periods.id = requisitions.periodid
   JOIN processing_schedules ON processing_schedules.id = processing_periods.scheduleid
   JOIN products ON products.code::text = requisition_line_items.productcode::text
   JOIN product_categories ON product_categories.id = products.categoryid
   JOIN programs ON programs.id = requisitions.programid
   JOIN requisition_group_members ON requisition_group_members.facilityid = facilities.id
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
   JOIN dosage_units ON dosage_units.id = products.dosageunitid
   JOIN product_forms ON product_forms.id = products.formid;

ALTER TABLE vw_requisition_detail
  OWNER TO postgres;