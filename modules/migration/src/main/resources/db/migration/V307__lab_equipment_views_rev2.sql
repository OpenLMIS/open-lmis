DROP VIEW IF EXISTS vw_equipment_operational_status;
CREATE OR REPLACE VIEW vw_equipment_operational_status AS
SELECT 
    geographic_zones.name AS district,
    facilities.name AS facility_name,
    equipments.name AS equipment_name,
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
 FROM facility_program_equipments 
 join equipments on  facility_program_equipments.equipmentid = equipments.id
 join equipment_status_line_items on equipments.code = equipment_status_line_items.code and facility_program_equipments.serialnumber = equipmentserial
 join requisitions on requisitions.id = equipment_status_line_items.rnrid
 JOIN programs ON facility_program_equipments.programid = programs.id
 JOIN facilities ON facilities.id = facility_program_equipments.facilityid
 JOIN facility_types ON facilities.typeid = facility_types.id
 JOIN equipment_operational_status ON equipment_operational_status.id = equipment_status_line_items.operationalstatusid
 JOIN processing_periods ON requisitions.periodid = processing_periods.id
 JOIN processing_schedules ON processing_periods.scheduleid = processing_schedules.id
 JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
 JOIN equipment_types ON equipment_types.id = equipments.equipmenttypeid
 JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
 ORDER BY geographic_zones.name, facilities.name, facility_program_equipments.model, equipment_status_line_items.operationalstatusid;
 
 ALTER TABLE vw_equipment_operational_status OWNER TO postgres;
 
 
 DROP VIEW IF EXISTS vw_equipment_list_by_donor;
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
 FROM facility_program_equipments 
 join equipments on  facility_program_equipments.equipmentid = equipments.id
 join equipment_status_line_items on equipments.code = equipment_status_line_items.code and facility_program_equipments.serialnumber = equipmentserial
 join requisitions on requisitions.id = equipment_status_line_items.rnrid
 JOIN programs ON facility_program_equipments.programid = programs.id
 JOIN facilities ON facilities.id = facility_program_equipments.facilityid
 JOIN facility_types ON facilities.typeid = facility_types.id
 JOIN equipment_operational_status ON equipment_operational_status.id = equipment_status_line_items.operationalstatusid
 JOIN processing_periods ON requisitions.periodid = processing_periods.id
 JOIN processing_schedules ON processing_periods.scheduleid = processing_schedules.id
 JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
 JOIN equipment_types ON equipment_types.id = equipments.equipmenttypeid
 JOIN donors ON donors.id = facility_program_equipments.primarydonorid
 JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
 ORDER BY geographic_zones.name, facilities.name, facility_program_equipments.model, equipment_status_line_items.operationalstatusid;
 
 ALTER TABLE vw_equipment_list_by_donor OWNER TO postgres;
