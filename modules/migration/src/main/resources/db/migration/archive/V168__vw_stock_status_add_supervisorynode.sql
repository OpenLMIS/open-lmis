
DROP VIEW IF EXISTS vw_stock_status;
CREATE OR REPLACE VIEW vw_stock_status AS
 SELECT fn_get_supplying_facility_name(requisitions.supervisorynodeid) AS supplyingfacility,
    facilities.code AS facilitycode,
    products.code as productCode,
    facilities.name AS facility, requisitions.status AS req_status,
    requisition_line_items.product, requisition_line_items.stockinhand,
    requisition_line_items.stockinhand + requisition_line_items.beginningbalance + requisition_line_items.quantitydispensed + requisition_line_items.quantityreceived + abs(requisition_line_items.totallossesandadjustments) AS reported_figures,
    requisitions.id as rnrid,
    requisition_line_items.amc,
        CASE
            WHEN COALESCE(requisition_line_items.amc, 0) = 0 THEN 0::numeric
            ELSE round((requisition_line_items.stockinhand / requisition_line_items.amc)::numeric, 1)
        END AS mos,
    COALESCE(
        CASE
            WHEN (COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth - requisition_line_items.stockinhand) < 0 THEN 0
            ELSE COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth - requisition_line_items.stockinhand
        END, 0) AS required,
        CASE
            WHEN requisition_line_items.stockinhand = 0 THEN 'SO'::text
            ELSE
            CASE
                WHEN requisition_line_items.stockinhand > 0 AND requisition_line_items.stockinhand::numeric <= (COALESCE(requisition_line_items.amc, 0)::numeric * facility_types.nominaleop) THEN 'US'::text
                ELSE
                CASE
                    WHEN requisition_line_items.stockinhand > (COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth) THEN 'OS'::text
                    ELSE 'SP'::text
                END
            END
        END AS status,
    facility_types.name AS facilitytypename, geographic_zones.id AS gz_id,
    geographic_zones.name AS location, products.id AS productid,
    processing_periods.startdate, programs.id AS programid,
    processing_schedules.id AS psid, processing_periods.enddate,
    processing_periods.id AS periodid, facility_types.id AS facilitytypeid,
    requisition_group_members.requisitiongroupid AS rgid,
    program_products.productCategoryId categoryid,
    products.tracer AS indicator_product, facilities.id AS facility_id,
    processing_periods.name AS processing_period_name,
    requisition_line_items.stockoutdays,
    requisitions.supervisorynodeid
   FROM requisition_line_items
   JOIN requisitions ON requisitions.id = requisition_line_items.rnrid
   JOIN facilities ON facilities.id = requisitions.facilityid
   JOIN facility_types ON facility_types.id = facilities.typeid
   JOIN processing_periods ON processing_periods.id = requisitions.periodid
   JOIN processing_schedules ON processing_schedules.id = processing_periods.scheduleid
   JOIN products ON products.code::text = requisition_line_items.productcode::text
   JOIN program_products ON requisitions.programId = program_products.programId and products.id = program_products.productId
   JOIN product_categories ON product_categories.id = program_products.productCategoryId
   JOIN programs ON programs.id = requisitions.programid
   JOIN requisition_group_members ON requisition_group_members.facilityid = facilities.id
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
  WHERE requisition_line_items.stockinhand IS NOT NULL AND requisition_line_items.skipped = false;