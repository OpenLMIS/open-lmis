 DROP VIEW IF EXISTS vw_lab_equipment_status;

 CREATE OR REPLACE VIEW vw_lab_equipment_status AS
 SELECT programs.name AS program,
    facilities.code AS facility_code,
    facilities.name AS facility_name,
    facility_types.name AS facility_type,
    vw_districts.district_name AS disrict,
    vw_districts.zone_name AS zone,
    equipment_types.name AS equipment_type,
    facility_program_equipments.model AS equipment_model,
    facility_program_equipments.serialnumber AS serial_number,
    equipments.name AS equipment_name,
    equipment_operational_status.name AS equipment_status,
    facility_program_equipments.hasservicecontract,
    facilities.latitude,
    facilities.longitude,
    facilities.id AS facility_id,
    programs.id AS programid,
    equipments.id AS equipment_id,
    equipment_operational_status.id AS status_id,
    facilities.geographiczoneid,
    facilities.typeid AS ftype_id,
    vw_districts.district_id,
    vw_districts.zone_id,
    vw_districts.region_id,
    vw_districts.parent,
    equipment_types.id AS equipmenttype_id
   FROM facility_program_equipments
   JOIN facilities ON facilities.id = facility_program_equipments.facilityid
   JOIN facility_types ON facility_types.id = facilities.typeid
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
   JOIN programs ON facility_program_equipments.programid = programs.id
   JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
   JOIN equipments ON equipments.id = facility_program_equipments.equipmentid
   JOIN equipment_types ON equipment_types.id = equipments.equipmenttypeid
   JOIN equipment_operational_status ON equipment_operational_status.id = facility_program_equipments.operationalstatusid
  ORDER BY facilities.name;

ALTER TABLE vw_lab_equipment_status
  OWNER TO postgres;