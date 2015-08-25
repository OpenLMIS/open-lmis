 DROP VIEW IF EXISTS vw_equipment_list_by_donor;

CREATE OR REPLACE VIEW vw_equipment_list_by_donor AS
 SELECT geographic_zones.name AS district,
    facilities.name AS facilityname,
    donors.shortname AS donor,
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
    facilities.id AS facility_id,
    programs.id AS programid,
    equipments.id AS equipment_id,
    equipment_operational_status.id AS status_id,
    equipment_types.id AS equipmenttype_id,
    facilities.geographiczoneid,
    facilities.typeid AS ftype_id,
    vw_districts.district_id,
    vw_districts.zone_id,
    vw_districts.region_id,
    vw_districts.parent,
    donors.id AS donorid
   FROM facility_program_equipments
   JOIN equipments ON facility_program_equipments.equipmentid = equipments.id
   JOIN programs ON facility_program_equipments.programid = programs.id
   JOIN facilities ON facilities.id = facility_program_equipments.facilityid
   JOIN facility_types ON facilities.typeid = facility_types.id
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
   JOIN equipment_types ON equipment_types.id = equipments.equipmenttypeid
   LEFT JOIN donors ON donors.id = facility_program_equipments.primarydonorid
   JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
   JOIN equipment_operational_status ON equipment_operational_status.id = facility_program_equipments.operationalstatusid
  ORDER BY geographic_zones.name, facilities.name, facility_program_equipments.model;

ALTER TABLE vw_equipment_list_by_donor
  OWNER TO postgres;
