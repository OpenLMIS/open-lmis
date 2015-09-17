 DROP VIEW IF EXISTS vw_lab_equipment_status;

 CREATE OR REPLACE VIEW vw_lab_equipment_status AS
 SELECT programs.name AS program,
    facilities.code AS facility_code,
    facilities.name AS facility_name,
    facility_types.name AS facility_type,
    vw_districts.district_name AS disrict,
    vw_districts.zone_name AS zone,
    equipment_types.name AS equipment_type,
    equipment_inventories.model AS equipment_model,
    equipment_inventories.serialnumber AS serial_number,
    equipments.name AS equipment_name,
    equipment_operational_status.name AS equipment_status,
    equipment_inventories.hasservicecontract,
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
   FROM equipment_inventories
   JOIN facilities ON facilities.id = equipment_inventories.facilityid
   JOIN facility_types ON facility_types.id = facilities.typeid
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
   JOIN programs ON equipment_inventories.programid = programs.id
   JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
   JOIN equipments ON equipments.id = equipment_inventories.equipmentid
   JOIN equipment_types ON equipment_types.id = equipments.equipmenttypeid
   JOIN equipment_operational_status ON equipment_operational_status.id = equipment_inventories.operationalstatusid
  ORDER BY facilities.name;

ALTER TABLE vw_lab_equipment_status
  OWNER TO postgres;