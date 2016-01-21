DROP VIEW vw_equipment_operational_status;

CREATE OR REPLACE VIEW vw_equipment_operational_status AS
  SELECT geographic_zones.name AS district,
    facilities.name AS facility_name,
    equipments.name AS equipment_name,
    equipments.model,
    equipment_inventories.serialnumber,
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
  FROM equipment_inventories
    JOIN equipments ON equipment_inventories.equipmentid = equipments.id
    JOIN equipment_types ON equipment_types.id = equipments.equipmenttypeid
    JOIN equipment_status_line_items ON equipment_types.code::text = equipment_status_line_items.code::text AND equipment_inventories.serialnumber::text = equipment_status_line_items.equipmentserial::text
    JOIN requisitions ON requisitions.id = equipment_status_line_items.rnrid
    JOIN programs ON equipment_inventories.programid = programs.id
    JOIN facilities ON facilities.id = equipment_inventories.facilityid
    JOIN facility_types ON facilities.typeid = facility_types.id
    JOIN equipment_operational_status ON equipment_operational_status.id = equipment_status_line_items.operationalstatusid
    JOIN processing_periods ON requisitions.periodid = processing_periods.id
    JOIN processing_schedules ON processing_periods.scheduleid = processing_schedules.id
    JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
    JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
  ORDER BY geographic_zones.name, facilities.name, equipments.model, equipment_status_line_items.operationalstatusid;

ALTER TABLE vw_equipment_operational_status
OWNER TO postgres;

ALTER TABLE equipments
DROP COLUMN code;
