-- View: vw_vaccine_cold_chain

DROP VIEW IF EXISTS vw_vaccine_cold_chain;

CREATE OR REPLACE VIEW vw_vaccine_cold_chain AS 
 SELECT processing_periods.id AS period_id,
    processing_periods.name AS period_name,
    processing_periods.startdate AS period_start_date,
    processing_periods.enddate AS period_end_date,
    geographic_zones.id AS geographic_zone_id,
    geographic_zones.name AS geographic_zone_name,
    geographic_zones.levelid AS level_id,
    geographic_zones.parentid AS parent_id,
    facilities.id AS facility_id,
    facilities.code AS facility_code,
    facilities.name AS facility_name,
    vaccine_reports.id AS report_id,
    equipments.model,
    equipment_types.name AS equipment_type_name,
    vaccine_report_cold_chain_line_items.mintemp,
    vaccine_report_cold_chain_line_items.maxtemp,
    vaccine_report_cold_chain_line_items.minepisodetemp,
    vaccine_report_cold_chain_line_items.maxepisodetemp,
    equipment_energy_types.name AS energy_source
   FROM vaccine_report_cold_chain_line_items
     JOIN vaccine_reports ON vaccine_reports.id = vaccine_report_cold_chain_line_items.reportid
     JOIN facilities ON vaccine_reports.facilityid = facilities.id
     JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
     JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
     JOIN equipment_inventories ON equipment_inventories.facilityid = facilities.id AND vaccine_report_cold_chain_line_items.equipmentinventoryid = equipment_inventories.id
     JOIN equipments ON equipment_inventories.equipmentid = equipments.id
     JOIN equipment_types ON equipments.equipmenttypeid = equipment_types.id
     JOIN equipment_energy_types ON equipments.energytypeid = equipment_energy_types.id;

ALTER TABLE vw_vaccine_cold_chain
  OWNER TO postgres;

