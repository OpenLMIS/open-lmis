CREATE OR REPLACE VIEW public.vw_district_consumption_summary AS
SELECT programs.id AS program_id,
    programs.name AS program_name,
    processing_periods.id AS processing_periods_id,
    processing_periods.name AS processing_periods_name,
    processing_periods.startdate AS processing_periods_start_date,
    processing_periods.enddate AS processing_periods_end_date,
    processing_schedules.id AS processing_schedules_id,
    processing_schedules.name AS processing_schedules_name,
    facility_types.name AS facility_type_name,
    facility_types.id AS facility_type_id,
    facilities.code AS facility_code,
    facilities.id AS facility_id,
    facilities.name AS facility_name,
    geographic_zones.name AS zone_name,
    geographic_zones.id AS zone_id,
    requisition_line_items.id AS requisition_line_item_id,
    requisition_line_items.productcode,
    requisition_line_items.product,
    products.id AS product_id,
    product_categories.name AS product_category_name,
    product_categories.id AS product_category_id,
    requisition_line_items.normalizedconsumption,
    requisition_line_items.quantitydispensed,
    requisition_line_items.id,
    vw_districts.zone_id AS district_zone_id,
    vw_districts.parent,
    vw_districts.region_id,
    vw_districts.district_id,
    products.tracer
   FROM requisition_line_items
     JOIN requisitions ON requisition_line_items.rnrid = requisitions.id
     JOIN products ON requisition_line_items.productcode::text = products.code::text
     JOIN programs ON requisitions.programid = programs.id
     JOIN program_products ON products.id = program_products.productid AND program_products.programid = programs.id
     JOIN processing_periods ON requisitions.periodid = processing_periods.id
     JOIN product_categories ON program_products.productcategoryid = product_categories.id
     JOIN processing_schedules ON processing_periods.scheduleid = processing_schedules.id
     JOIN facilities ON requisitions.facilityid = facilities.id
     JOIN facility_types ON facilities.typeid = facility_types.id
     JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
     JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id;
