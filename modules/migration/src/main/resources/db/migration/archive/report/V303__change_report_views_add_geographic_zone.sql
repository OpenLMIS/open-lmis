DROP VIEW IF EXISTS vw_equipment_list_by_donor;
DROP VIEW IF EXISTS vw_equipment_operational_status;
DROP VIEW IF EXISTS vw_district_consumption_summary;

-- vw_equipment_list_by_donor
CREATE OR REPLACE VIEW vw_equipment_list_by_donor AS
 SELECT geographic_zones.name AS district,
    facilities.name AS facilityname,
    donors.longname AS donor,
    facility_program_equipments.sourceoffund,
    equipments.name AS equipment_name,
    facility_program_equipments.model,
    facility_program_equipments.yearofinstallation,
        CASE
            WHEN facility_program_equipments.hasservicecontract = false THEN 'No'::text
            ELSE 'yes'::text
        END AS hasservicecontract,
        CASE
            WHEN facility_program_equipments.servicecontractenddate IS NULL THEN '-'::text
            ELSE facility_program_equipments.servicecontractenddate::text
        END AS servicecontractenddate,
        CASE
            WHEN facility_program_equipments.isactive = true THEN 'Yes'::text
            ELSE 'No'::text
        END AS isactive,
        CASE
            WHEN facility_program_equipments.datedecommissioned IS NULL THEN '-'::text
            ELSE facility_program_equipments.datedecommissioned::text
        END AS datedecommissioned,
        CASE
            WHEN facility_program_equipments.replacementrecommended = false THEN 'No'::text
            ELSE 'Yes'::text
        END AS replacementrecommended,
    facility_types.id AS ft_id,
    programs.id AS pg_id,
    facilities.id AS f_id,
    processing_schedules.id AS ps_id,
    processing_periods.id AS pp_id,
    equipment_types.id AS eqpt_ty_id,
    vw_districts.zone_id,
    vw_districts.parent,
    vw_districts.region_id,
    vw_districts.district_id
   FROM equipments
   JOIN facility_program_equipments ON facility_program_equipments.equipmentid = equipments.id
   JOIN programs ON facility_program_equipments.programid = programs.id
   JOIN facilities ON facilities.id = facility_program_equipments.facilityid
   JOIN facility_types ON facilities.typeid = facility_types.id
   JOIN equipment_status_line_items ON equipments.code::text = equipment_status_line_items.code::text
   JOIN equipment_operational_status ON equipment_operational_status.id = equipment_status_line_items.operationalstatusid
   JOIN requisitions ON requisitions.programid = programs.id AND equipment_status_line_items.rnrid = requisitions.id
   JOIN processing_periods ON requisitions.periodid = processing_periods.id
   JOIN processing_schedules ON processing_periods.scheduleid = processing_schedules.id
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
   JOIN equipment_types ON equipment_types.id = equipments.equipmenttypeid
   JOIN donors ON donors.id = facility_program_equipments.primarydonorid
   JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
  ORDER BY geographic_zones.name, facilities.name, equipment_status_line_items.operationalstatusid;

  --  vw_equipment_operational_status

CREATE OR REPLACE VIEW vw_equipment_operational_status AS
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
    equipment_types.id AS eqpt_ty_id,
    vw_districts.zone_id,
    vw_districts.parent,
    vw_districts.region_id,
    vw_districts.district_id
   FROM equipments
   JOIN facility_program_equipments ON facility_program_equipments.equipmentid = equipments.id
   JOIN programs ON facility_program_equipments.programid = programs.id
   JOIN facilities ON facilities.id = facility_program_equipments.facilityid
   JOIN facility_types ON facilities.typeid = facility_types.id
   JOIN equipment_status_line_items ON equipments.code::text = equipment_status_line_items.code::text
   JOIN equipment_operational_status ON equipment_operational_status.id = equipment_status_line_items.operationalstatusid
   JOIN requisitions ON requisitions.programid = programs.id AND equipment_status_line_items.rnrid = requisitions.id
   JOIN processing_periods ON requisitions.periodid = processing_periods.id
   JOIN processing_schedules ON processing_periods.scheduleid = processing_schedules.id
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
   JOIN equipment_types ON equipment_types.id = equipments.equipmenttypeid
   JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
  ORDER BY geographic_zones.name, facilities.name, equipment_status_line_items.operationalstatusid;

-- View: vw_district_consumption_summary

CREATE OR REPLACE VIEW vw_district_consumption_summary AS
  SELECT
        programs.id AS program_id,
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
        vw_districts.district_id
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


ALTER TABLE vw_equipment_list_by_donor OWNER TO postgres;
ALTER TABLE vw_equipment_operational_status OWNER TO postgres;
ALTER TABLE vw_district_consumption_summary OWNER TO postgres;
