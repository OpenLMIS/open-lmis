-- View: vw_stock_status_2
-- View: dw_product_facility_stock_info_vw

 DROP VIEW IF EXISTS dw_product_facility_stock_info_vw;
 
 DROP VIEW IF EXISTS vw_stock_status_2;
 
CREATE OR REPLACE VIEW vw_stock_status_2 AS 
 SELECT facilities.code AS facilitycode,
    products.code AS productcode,
    facilities.name AS facility,
    requisitions.status AS req_status,
    requisition_line_items.product,
    requisition_line_items.stockinhand,
    requisition_line_items.stockinhand + requisition_line_items.beginningbalance + requisition_line_items.quantitydispensed + requisition_line_items.quantityreceived + abs(requisition_line_items.totallossesandadjustments) AS reported_figures,
    requisitions.id AS rnrid,
    requisition_line_items.amc,
        CASE
            WHEN COALESCE(requisition_line_items.amc, 0) = 0 THEN 0::numeric
            ELSE round((requisition_line_items.stockinhand / requisition_line_items.amc::float)::numeric,2)
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
    facility_types.name AS facilitytypename,
    geographic_zones.id AS gz_id,
    geographic_zones.name AS location,
    products.id AS productid,
    processing_periods.startdate,
    programs.id AS programid,
    processing_schedules.id AS psid,
    processing_periods.enddate,
    processing_periods.id AS periodid,
    facility_types.id AS facilitytypeid,
    program_products.productcategoryid AS categoryid,
    products.tracer AS indicator_product,
    facilities.id AS facility_id,
    processing_periods.name AS processing_period_name,
    requisition_line_items.stockoutdays,
    0 AS supervisorynodeid
   FROM requisition_line_items
     JOIN requisitions ON requisitions.id = requisition_line_items.rnrid
     JOIN facilities ON facilities.id = requisitions.facilityid
     JOIN facility_types ON facility_types.id = facilities.typeid
     JOIN processing_periods ON processing_periods.id = requisitions.periodid
     JOIN processing_schedules ON processing_schedules.id = processing_periods.scheduleid
     JOIN products ON products.code::text = requisition_line_items.productcode::text
     JOIN program_products ON requisitions.programid = program_products.programid AND products.id = program_products.productid
     JOIN product_categories ON product_categories.id = program_products.productcategoryid
     JOIN programs ON programs.id = requisitions.programid
     JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
  WHERE requisition_line_items.stockinhand IS NOT NULL AND requisition_line_items.skipped = false;

ALTER TABLE vw_stock_status_2
  OWNER TO postgres;




CREATE OR REPLACE VIEW dw_product_facility_stock_info_vw AS 
 SELECT 0 AS requisitiongroupid,
    vw_stock_status_2.programid,
    vw_stock_status_2.periodid,
    vw_stock_status_2.gz_id AS geographiczoneid,
    vw_stock_status_2.location AS geographiczonename,
    vw_stock_status_2.facility_id AS facilityid,
    vw_stock_status_2.facility AS facilityname,
    vw_stock_status_2.facilitycode,
    vw_stock_status_2.productid,
    vw_stock_status_2.product AS primaryname,
    vw_stock_status_2.amc,
    vw_stock_status_2.stockinhand AS soh,
    vw_stock_status_2.mos,
    "left"(vw_stock_status_2.status, 1) AS stocking
   FROM vw_stock_status_2
  ORDER BY vw_stock_status_2.gz_id, vw_stock_status_2.programid, vw_stock_status_2.periodid, vw_stock_status_2.productid, vw_stock_status_2.product, vw_stock_status_2.status;

ALTER TABLE dw_product_facility_stock_info_vw
  OWNER TO postgres;

