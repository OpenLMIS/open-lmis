/*
script to update views
 - remove unncessary joins
 - remove distict where not needed
 - add geographic_zones in join
 - remove requisition groups and requisition_group_members
Date: 2014-07-21 12:09:12
*/


DROP VIEW IF EXISTS vw_district_consumption_summary;
DROP VIEW IF EXISTS vw_district_financial_summary;
DROP VIEW IF EXISTS vw_equipment_list_by_donor;
DROP VIEW IF EXISTS vw_equipment_operational_status;
DROP VIEW IF EXISTS vw_number_rnr_created_by_facility;
DROP VIEW IF EXISTS vw_order_fill_rate;
DROP VIEW IF EXISTS vw_order_fill_rate_details;
DROP VIEW IF EXISTS vw_regimen_district_distribution;
DROP VIEW IF EXISTS vw_regimen_summary;
DROP VIEW IF EXISTS vw_rnr_feedback;
DROP VIEW IF EXISTS vw_requisition_detail;
DROP VIEW IF EXISTS vw_rnr_status;
DROP VIEW IF EXISTS vw_rnr_status_details;
DROP VIEW IF EXISTS vw_stock_status;
DROP VIEW IF EXISTS vw_supply_status;


-- ----------------------------
-- View structure for "vw_district_consumption_summary"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_district_consumption_summary" AS 
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
    requisition_line_items.id
   FROM ((((((((((requisition_line_items
   JOIN requisitions ON ((requisition_line_items.rnrid = requisitions.id)))
   JOIN products ON (((requisition_line_items.productcode)::text = (products.code)::text)))
   JOIN programs ON ((requisitions.programid = programs.id)))
   JOIN program_products ON (((products.id = program_products.productid) AND (program_products.programid = programs.id))))
   JOIN processing_periods ON ((requisitions.periodid = processing_periods.id)))
   JOIN product_categories ON ((program_products.productcategoryid = product_categories.id)))
   JOIN processing_schedules ON ((processing_periods.scheduleid = processing_schedules.id)))
   JOIN facilities ON ((requisitions.facilityid = facilities.id)))
   JOIN facility_types ON ((facilities.typeid = facility_types.id)))
   JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)));

-- ----------------------------
-- View structure for "vw_district_financial_summary"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_district_financial_summary" AS 
 SELECT processing_periods.id AS periodid,
    processing_periods.name AS period,
    processing_periods.startdate,
    processing_periods.enddate,
    processing_periods.scheduleid,
    processing_schedules.name AS schedule,
    facility_types.id AS facilitytypeid,
    facility_types.name AS facilitytype,
    facilities.code AS facilitycode,
    facilities.name AS facility,
    facilities.id AS facility_id,
    requisitions.id AS rnrid,
    requisitions.status,
    geographic_zones.id AS zoneid,
    geographic_zones.name AS region,
    p.name AS program,
    p.id AS programid,
    requisitions.fullsupplyitemssubmittedcost,
    requisitions.nonfullsupplyitemssubmittedcost
   FROM ((((((requisitions
   JOIN facilities ON ((facilities.id = requisitions.facilityid)))
   JOIN facility_types ON ((facility_types.id = facilities.typeid)))
   JOIN processing_periods ON ((processing_periods.id = requisitions.periodid)))
   JOIN processing_schedules ON ((processing_schedules.id = processing_periods.scheduleid)))
   JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)))
   JOIN programs p ON ((p.id = requisitions.programid)));

-- ----------------------------
-- View structure for "vw_equipment_list_by_donor"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_equipment_list_by_donor" AS 
 SELECT geographic_zones.name AS district,
    facilities.name AS facilityname,
    donors.longname AS donor,
    facility_program_equipments.sourceoffund,
    equipments.name AS equipment_name,
    facility_program_equipments.model,
    facility_program_equipments.yearofinstallation,
        CASE
            WHEN (facility_program_equipments.hasservicecontract = false) THEN 'No'::text
            ELSE 'yes'::text
        END AS hasservicecontract,
        CASE
            WHEN (facility_program_equipments.servicecontractenddate IS NULL) THEN '-'::text
            ELSE (facility_program_equipments.servicecontractenddate)::text
        END AS servicecontractenddate,
        CASE
            WHEN (facility_program_equipments.isactive = true) THEN 'Yes'::text
            ELSE 'No'::text
        END AS isactive,
        CASE
            WHEN (facility_program_equipments.datedecommissioned IS NULL) THEN '-'::text
            ELSE (facility_program_equipments.datedecommissioned)::text
        END AS datedecommissioned,
        CASE
            WHEN (facility_program_equipments.replacementrecommended = false) THEN 'No'::text
            ELSE 'Yes'::text
        END AS replacementrecommended,
    facility_types.id AS ft_id,
    programs.id AS pg_id,
    facilities.id AS f_id,
    processing_schedules.id AS ps_id,
    processing_periods.id AS pp_id,
    equipment_types.id AS eqpt_ty_id
   FROM ((((((((((((equipments
   JOIN facility_program_equipments ON ((facility_program_equipments.equipmentid = equipments.id)))
   JOIN programs ON ((facility_program_equipments.programid = programs.id)))
   JOIN facilities ON ((facilities.id = facility_program_equipments.facilityid)))
   JOIN facility_types ON ((facilities.typeid = facility_types.id)))
   JOIN equipment_status_line_items ON (((equipments.code)::text = (equipment_status_line_items.code)::text)))
   JOIN equipment_operational_status ON ((equipment_operational_status.id = equipment_status_line_items.operationalstatusid)))
   JOIN requisitions ON (((requisitions.programid = programs.id) AND (equipment_status_line_items.rnrid = requisitions.id))))
   JOIN processing_periods ON ((requisitions.periodid = processing_periods.id)))
   JOIN processing_schedules ON ((processing_periods.scheduleid = processing_schedules.id)))
   JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)))
   JOIN equipment_types ON ((equipment_types.id = equipments.equipmenttypeid)))
   JOIN donors ON ((donors.id = facility_program_equipments.primarydonorid)))
  ORDER BY geographic_zones.name, facilities.name, equipment_status_line_items.operationalstatusid;

-- ----------------------------
-- View structure for "vw_equipment_operational_status"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_equipment_operational_status" AS 
 SELECT facilities.name AS facility_name,
    equipments.name AS equipment_name,
    geographic_zones.name AS district,
    facility_program_equipments.model,
    facility_program_equipments.serialnumber,
    equipment_status_line_items.testcount AS test,
    equipment_status_line_items.totalcount AS total_test,
    equipment_status_line_items.daysoutofuse,
    equipment_operational_status.name AS operational_status,
    facility_types.id AS ft_id,
    programs.id AS pg_id,
    facilities.id AS f_id,
    processing_schedules.id AS ps_id,
    processing_periods.id AS pp_id,
    equipment_types.id AS eqpt_ty_id
   FROM (((((((((((equipments
   JOIN facility_program_equipments ON ((facility_program_equipments.equipmentid = equipments.id)))
   JOIN programs ON ((facility_program_equipments.programid = programs.id)))
   JOIN facilities ON ((facilities.id = facility_program_equipments.facilityid)))
   JOIN facility_types ON ((facilities.typeid = facility_types.id)))
   JOIN equipment_status_line_items ON (((equipments.code)::text = (equipment_status_line_items.code)::text)))
   JOIN equipment_operational_status ON ((equipment_operational_status.id = equipment_status_line_items.operationalstatusid)))
   JOIN requisitions ON (((requisitions.programid = programs.id) AND (equipment_status_line_items.rnrid = requisitions.id))))
   JOIN processing_periods ON ((requisitions.periodid = processing_periods.id)))
   JOIN processing_schedules ON ((processing_periods.scheduleid = processing_schedules.id)))
   JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)))
   JOIN equipment_types ON ((equipment_types.id = equipments.equipmenttypeid)))
  ORDER BY geographic_zones.name, facilities.name, equipment_status_line_items.operationalstatusid;

-- ----------------------------
-- View structure for "vw_number_rnr_created_by_facility"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_number_rnr_created_by_facility" AS 
 SELECT count(r.status) AS totalstatus,
    r.status,
    gz.id AS geographiczoneid,
    gz.name AS geographiczonename
   FROM (((facilities f
   JOIN requisitions r ON ((r.facilityid = f.id)))
   JOIN programs p ON ((p.id = r.programid)))
   JOIN geographic_zones gz ON ((gz.id = f.geographiczoneid)))
  WHERE (r.id IN ( SELECT requisition_status_changes.rnrid
   FROM requisition_status_changes
  GROUP BY requisition_status_changes.rnrid, requisition_status_changes.status
 HAVING (count(*) > 0)))
  GROUP BY r.status, gz.id
  ORDER BY r.status;

-- ----------------------------
-- View structure for "vw_order_fill_rate"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_order_fill_rate" AS 
 SELECT dw_orders.status,
    dw_orders.facilityid,
    dw_orders.periodid,
    dw_orders.productprimaryname AS product,
    products.code AS productcode,
    facilities.name AS facilityname,
    dw_orders.scheduleid,
    dw_orders.facilitytypeid,
    dw_orders.productid,
    dw_orders.productcategoryid,
    dw_orders.programid,
    dw_orders.geographiczoneid AS zoneid,
    dw_orders.geographiczonename AS zonename,
    sum((COALESCE(dw_orders.quantityapproved, 0))::numeric) AS quantityapproved,
    sum((COALESCE(dw_orders.quantityreceived, 0))::numeric) AS quantityreceived,
    sum(
        CASE
            WHEN (COALESCE(dw_orders.quantityapproved, 0) = 0) THEN (0)::numeric
            ELSE
            CASE
                WHEN (dw_orders.quantityapproved > 0) THEN (1)::numeric
                ELSE (0)::numeric
            END
        END) AS totalproductsapproved,
    sum(
        CASE
            WHEN (COALESCE(dw_orders.quantityreceived, 0) = 0) THEN (0)::numeric
            ELSE
            CASE
                WHEN (dw_orders.quantityreceived > 0) THEN (1)::numeric
                ELSE (0)::numeric
            END
        END) AS totalproductsreceived,
    sum(
        CASE
            WHEN ((COALESCE(dw_orders.quantityreceived, 0) > 1) AND (COALESCE(dw_orders.quantityapproved, 0) = 0)) THEN (1)::numeric
            ELSE (0)::numeric
        END) AS totalproductspushed
   FROM ((dw_orders
   JOIN products ON (((products.id = dw_orders.productid) AND ((products.primaryname)::text = (dw_orders.productprimaryname)::text))))
   JOIN facilities ON ((facilities.id = dw_orders.facilityid)))
  WHERE ((dw_orders.status)::text = ANY (ARRAY[('RELEASED'::character varying)::text]))
  GROUP BY dw_orders.scheduleid, dw_orders.facilitytypeid, dw_orders.productid, dw_orders.status, dw_orders.facilityid, dw_orders.periodid, dw_orders.productprimaryname, products.code, facilities.name, dw_orders.productcategoryid, dw_orders.programid, dw_orders.geographiczoneid, dw_orders.geographiczonename;

-- ----------------------------
-- View structure for "vw_order_fill_rate_details"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_order_fill_rate_details" AS 
 SELECT r.programid,
    programs.name AS program,
    li.productcategory AS category,
    prc.id AS categoryid,
    pp.id AS periodid,
    pp.name AS period,
    ps.id AS scheduleid,
    ps.name AS schedule,
    ft.id AS facilitytypeid,
    count(li.quantityapproved) AS total,
    ft.name AS facilitytype,
    r.id AS req_id,
    f.id AS facilityid,
    f.name AS facility,
    li.productcode,
    li.product,
    pr.id AS productid,
    gz.id AS zoneid,
    gz.name AS region,
    li.quantityreceived AS receipts,
    li.quantityapproved AS approved
   FROM ((((((((((requisition_line_items li
   JOIN requisitions r ON ((r.id = li.rnrid)))
   JOIN facilities f ON ((r.facilityid = f.id)))
   JOIN facility_types ft ON ((ft.id = f.typeid)))
   JOIN processing_periods pp ON ((pp.id = r.periodid)))
   JOIN products pr ON (((pr.code)::text = (li.productcode)::text)))
   JOIN geographic_zones gz ON ((gz.id = f.geographiczoneid)))
   JOIN program_products ON (((r.programid = program_products.programid) AND (pr.id = program_products.productid))))
   JOIN product_categories prc ON ((prc.id = program_products.productcategoryid)))
   JOIN programs ON ((r.programid = programs.id)))
   JOIN processing_schedules ps ON ((ps.id = pp.scheduleid)))
  GROUP BY li.product, r.id, li.productcategory, f.name, ft.name, li.productcode, li.quantityapproved, li.quantityreceived, gz.name, r.programid, programs.name, prc.id, pp.id, ps.id, ft.id, f.id, pr.id, gz.id;

-- ----------------------------
-- View structure for "vw_regimen_district_distribution"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_regimen_district_distribution" AS 
 SELECT DISTINCT r.programid,
    ps.id AS scheduleid,
    pp.id AS periodid,
    regimens.categoryid,
    regimens.id AS regimenid,
    regimens.name AS regimen,
    gz.name AS district,
    li.patientsontreatment,
    li.patientstoinitiatetreatment,
    li.patientsstoppedtreatment,
    r.facilityid,
    r.status,
    f.name AS facilityname,
    f.typeid AS facilitytypeid
   FROM (((((((((regimen_line_items li
   JOIN requisitions r ON ((li.rnrid = r.id)))
   JOIN facilities f ON ((r.facilityid = f.id)))
   JOIN facility_types ft ON ((f.typeid = ft.id)))
   JOIN geographic_zones gz ON ((gz.id = f.geographiczoneid)))
   JOIN geographic_zones zone ON ((gz.parentid = zone.id)))
   JOIN geographic_zones c ON ((zone.parentid = c.id)))
   JOIN processing_periods pp ON ((r.periodid = pp.id)))
   JOIN processing_schedules ps ON ((pp.scheduleid = ps.id)))
   JOIN regimens ON ((r.programid = regimens.programid)));

-- ----------------------------
-- View structure for "vw_regimen_summary"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_regimen_summary" AS 
 SELECT r.programid,
    ps.id AS scheduleid,
    pp.id AS periodid,
    li.regimencategory,
    regimens.categoryid,
    regimens.id AS regimenid,
    regimens.name AS regimen,
    li.patientsontreatment,
    li.patientstoinitiatetreatment,
    li.patientsstoppedtreatment,
    r.status,
    geographic_zones.id,
    geographic_zones.name
   FROM ((((((regimen_line_items li
   JOIN requisitions r ON ((r.id = li.rnrid)))
   JOIN processing_periods pp ON ((pp.id = r.periodid)))
   JOIN processing_schedules ps ON ((ps.id = pp.scheduleid)))
   JOIN regimens ON ((regimens.programid = r.programid)))
   JOIN facilities ON ((facilities.id = r.facilityid)))
   JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)));

-- ----------------------------
-- View structure for "vw_requisition_detail"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_requisition_detail" AS 
 SELECT programs.id AS program_id,
    programs.name AS program_name,
    products.id AS product_id,
    products.code AS product_code,
    products.primaryname AS product_primaryname,
    products.description AS product_description,
    products.tracer AS indicator_product,
    processing_periods.id AS processing_periods_id,
    processing_periods.name AS processing_periods_name,
    processing_periods.startdate AS processing_periods_start_date,
    processing_periods.enddate AS processing_periods_end_date,
    processing_periods.scheduleid AS processing_schedules_id,
    facility_types.id AS facility_type_id,
    facility_types.name AS facility_type_name,
    facilities.code AS facility_code,
    facilities.name AS facility_name,
    requisition_line_items.productcode,
    requisition_line_items.product,
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
    facilities.id AS facility_id,
    requisitions.id AS req_id,
    requisitions.status AS req_status,
    requisition_line_items.id AS req_line_id,
    geographic_zones.id AS zone_id,
    geographic_zones.name AS region,
    facility_types.nominalmaxmonth,
    facility_types.nominaleop,
    dosage_units.code AS du_code,
    product_forms.code AS pf_code,
    products.dispensingunit,
    program_products.productcategoryid AS categoryid,
    products.productgroupid,
    processing_periods.scheduleid,
    requisitions.emergency
   FROM ((((((((((((requisition_line_items
   JOIN requisitions ON ((requisition_line_items.rnrid = requisitions.id)))
   JOIN products ON (((requisition_line_items.productcode)::text = (products.code)::text)))
   JOIN programs ON ((requisitions.programid = programs.id)))
   JOIN program_products ON (((products.id = program_products.productid) AND (program_products.programid = programs.id))))
   JOIN processing_periods ON ((requisitions.periodid = processing_periods.id)))
   JOIN product_categories ON ((program_products.productcategoryid = product_categories.id)))
   JOIN processing_schedules ON ((processing_periods.scheduleid = processing_schedules.id)))
   JOIN facilities ON ((requisitions.facilityid = facilities.id)))
   JOIN facility_types ON ((facilities.typeid = facility_types.id)))
   JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)))
   JOIN product_forms ON ((products.formid = product_forms.id)))
   JOIN dosage_units ON ((products.dosageunitid = dosage_units.id)));

-- ----------------------------
-- View structure for "vw_rnr_feedback"
-- ----------------------------

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


-- ----------------------------
-- View structure for "vw_rnr_status"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_rnr_status" AS 
 SELECT p.name AS programname,
    r.programid,
    r.periodid,
    f.id AS facilityid,
    r.id AS rnrid,
    r.status,
    gz.name AS geographiczonename
   FROM ((((facilities f
   JOIN requisitions r ON ((r.facilityid = f.id)))
   JOIN programs p ON ((p.id = r.programid)))
   JOIN requisition_status_changes ON ((r.id = requisition_status_changes.rnrid)))
   JOIN geographic_zones gz ON ((gz.id = f.geographiczoneid)));

-- ----------------------------
-- View structure for "vw_rnr_status_details"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_rnr_status_details" AS 
 SELECT p.name AS programname,
    r.programid,
    r.periodid,
    ps.name AS periodname,
    r.createddate,
    f.code AS facilitycode,
    f.name AS facilityname,
    f.id AS facilityid,
    r.id AS rnrid,
    r.status,
    ft.name AS facilitytypename,
    gz.id AS geographiczoneid,
    gz.name AS geographiczonename
   FROM ((((((facilities f
   JOIN requisitions r ON ((r.facilityid = f.id)))
   JOIN programs p ON ((p.id = r.programid)))
   JOIN processing_periods ps ON ((ps.id = r.periodid)))
   JOIN requisition_status_changes ON ((r.id = requisition_status_changes.rnrid)))
   JOIN facility_types ft ON ((ft.id = f.typeid)))
   JOIN geographic_zones gz ON ((gz.id = f.geographiczoneid)));

-- ----------------------------
-- View structure for "vw_stock_status"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_stock_status" AS 
 SELECT fn_get_supplying_facility_name(requisitions.supervisorynodeid) AS supplyingfacility,
    facilities.code AS facilitycode,
    products.code AS productcode,
    facilities.name AS facility,
    requisitions.status AS req_status,
    requisition_line_items.product,
    requisition_line_items.stockinhand,
    ((((requisition_line_items.stockinhand + requisition_line_items.beginningbalance) + requisition_line_items.quantitydispensed) + requisition_line_items.quantityreceived) + abs(requisition_line_items.totallossesandadjustments)) AS reported_figures,
    requisitions.id AS rnrid,
    requisition_line_items.amc,
        CASE
            WHEN (COALESCE(requisition_line_items.amc, 0) = 0) THEN (0)::numeric
            ELSE ((requisition_line_items.stockinhand)::numeric / (requisition_line_items.amc)::numeric)
        END AS mos,
    COALESCE(
        CASE
            WHEN (((COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth) - requisition_line_items.stockinhand) < 0) THEN 0
            ELSE ((COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth) - requisition_line_items.stockinhand)
        END, 0) AS required,
        CASE
            WHEN (requisition_line_items.stockinhand = 0) THEN 'SO'::text
            ELSE
            CASE
                WHEN ((requisition_line_items.stockinhand > 0) AND ((requisition_line_items.stockinhand)::numeric <= ((COALESCE(requisition_line_items.amc, 0))::numeric * facility_types.nominaleop))) THEN 'US'::text
                ELSE
                CASE
                    WHEN (requisition_line_items.stockinhand > (COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth)) THEN 'OS'::text
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
    requisitions.supervisorynodeid
   FROM ((((((((((requisition_line_items
   JOIN requisitions ON ((requisitions.id = requisition_line_items.rnrid)))
   JOIN facilities ON ((facilities.id = requisitions.facilityid)))
   JOIN facility_types ON ((facility_types.id = facilities.typeid)))
   JOIN processing_periods ON ((processing_periods.id = requisitions.periodid)))
   JOIN processing_schedules ON ((processing_schedules.id = processing_periods.scheduleid)))
   JOIN products ON (((products.code)::text = (requisition_line_items.productcode)::text)))
   JOIN program_products ON (((requisitions.programid = program_products.programid) AND (products.id = program_products.productid))))
   JOIN product_categories ON ((product_categories.id = program_products.productcategoryid)))
   JOIN programs ON ((programs.id = requisitions.programid)))
   JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)))
  WHERE ((requisition_line_items.stockinhand IS NOT NULL) AND (requisition_line_items.skipped = false));

-- ----------------------------
-- View structure for "vw_supply_status"
-- ----------------------------
CREATE OR REPLACE VIEW "vw_supply_status" AS 
 SELECT requisition_line_items.id AS li_id,
    requisition_line_items.rnrid AS li_rnrid,
    requisition_line_items.productcode AS li_productcode,
    requisition_line_items.product AS li_product,
    requisition_line_items.productdisplayorder AS li_productdisplayorder,
    requisition_line_items.productcategory AS li_productcategory,
    requisition_line_items.productcategorydisplayorder AS li_productcategorydisplayorder,
    requisition_line_items.dispensingunit AS li_dispensingunit,
    requisition_line_items.beginningbalance AS li_beginningbalance,
    requisition_line_items.quantityreceived AS li_quantityreceived,
    requisition_line_items.quantitydispensed AS li_quantitydispensed,
    requisition_line_items.stockinhand AS li_stockinhand,
    requisition_line_items.quantityrequested AS li_quantityrequested,
    requisition_line_items.reasonforrequestedquantity AS li_reasonforrequestedquantity,
    requisition_line_items.calculatedorderquantity AS li_calculatedorderquantity,
    requisition_line_items.quantityapproved AS li_quantityapproved,
    requisition_line_items.totallossesandadjustments AS li_totallossesandadjustments,
    requisition_line_items.newpatientcount AS li_newpatientcount,
    requisition_line_items.stockoutdays AS li_stockoutdays,
    requisition_line_items.normalizedconsumption AS li_normalizedconsumption,
    requisition_line_items.amc AS li_amc,
    requisition_line_items.maxmonthsofstock AS li_maxmonthsofstock,
    requisition_line_items.maxstockquantity AS li_maxstockquantity,
    requisition_line_items.packstoship AS li_packstoship,
    requisition_line_items.price AS li_price,
    requisition_line_items.expirationdate AS li_expirationdate,
    requisition_line_items.remarks AS li_remarks,
    requisition_line_items.dosespermonth AS li_dosespermonth,
    requisition_line_items.dosesperdispensingunit AS li_dosesperdispensingunit,
    requisition_line_items.packsize AS li_packsize,
    requisition_line_items.roundtozero AS li_roundtozero,
    requisition_line_items.packroundingthreshold AS li_packroundingthreshold,
    requisition_line_items.fullsupply AS li_fullsupply,
    requisition_line_items.createdby AS li_createdby,
    requisition_line_items.createddate AS li_createddate,
    requisition_line_items.modifiedby AS li_modifiedby,
    requisition_line_items.modifieddate AS li_modifieddate,
    programs.id AS pg_id,
    programs.code AS pg_code,
    programs.name AS pg_name,
    products.id AS p_id,
    products.code AS p_code,
    products.primaryname AS p_primaryname,
    program_products.displayorder AS p_displayorder,
    products.tracer AS indicator_product,
    products.description AS p_description,
    facility_types.name AS facility_type_name,
    facility_types.id AS ft_id,
    facility_types.code AS ft_code,
    facility_types.nominalmaxmonth AS ft_nominalmaxmonth,
    facility_types.nominaleop AS ft_nominaleop,
    facilities.id AS f_id,
    facilities.code AS f_code,
    facilities.name AS facility,
    fn_get_supplying_facility_name(requisitions.supervisorynodeid) AS supplyingfacility,
    facilities.geographiczoneid AS f_zoneid,
    facility_approved_products.maxmonthsofstock AS fp_maxmonthsofstock,
    facility_approved_products.minmonthsofstock AS fp_minmonthsofstock,
    facility_approved_products.eop AS fp_eop,
    requisitions.status AS r_status,
    requisitions.supervisorynodeid,
    processing_schedules.id AS ps_id,
    processing_periods.id AS pp_id,
    geographic_zones.id AS geographiczoneid,
    geographic_zones.name AS geographiczonename
   FROM (((((((((((requisition_line_items
   JOIN requisitions ON ((requisitions.id = requisition_line_items.rnrid)))
   JOIN facilities ON ((facilities.id = requisitions.facilityid)))
   JOIN facility_types ON ((facility_types.id = facilities.typeid)))
   JOIN processing_periods ON ((processing_periods.id = requisitions.periodid)))
   JOIN processing_schedules ON ((processing_schedules.id = processing_periods.scheduleid)))
   JOIN products ON (((products.code)::text = (requisition_line_items.productcode)::text)))
   JOIN program_products ON (((requisitions.programid = program_products.programid) AND (products.id = program_products.productid))))
   JOIN product_categories ON ((product_categories.id = program_products.productcategoryid)))
   JOIN programs ON ((programs.id = requisitions.programid)))
   JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)))
   JOIN facility_approved_products ON (((facility_types.id = facility_approved_products.facilitytypeid) AND (facility_approved_products.programproductid = program_products.id))));


ALTER TABLE vw_district_consumption_summary OWNER TO postgres;
ALTER TABLE vw_district_financial_summary OWNER TO postgres;
ALTER TABLE vw_equipment_list_by_donor OWNER TO postgres;
ALTER TABLE vw_equipment_operational_status OWNER TO postgres;
ALTER TABLE vw_number_rnr_created_by_facility OWNER TO postgres;
ALTER TABLE vw_order_fill_rate OWNER TO postgres;
ALTER TABLE vw_order_fill_rate_details OWNER TO postgres;
ALTER TABLE vw_regimen_district_distribution OWNER TO postgres;
ALTER TABLE vw_regimen_summary OWNER TO postgres;
ALTER TABLE vw_rnr_feedback OWNER TO postgres;
ALTER TABLE vw_requisition_detail OWNER TO postgres;
ALTER TABLE vw_rnr_status OWNER TO postgres;
ALTER TABLE vw_rnr_status_details OWNER TO postgres;
ALTER TABLE vw_stock_status OWNER TO postgres;
ALTER TABLE vw_supply_status OWNER TO postgres;
-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------
