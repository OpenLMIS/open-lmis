-- View: vw_vaccine_cold_chain

DROP VIEW IF EXISTS vw_vaccine_cold_chain;

CREATE OR REPLACE VIEW vw_vaccine_cold_chain AS 
 SELECT pp.id AS period_id,
    pp.name AS period_name,
    pp.startdate AS period_start_date,
    pp.enddate AS period_end_date,
    gz.id AS geographic_zone_id,
    gz.name AS geographic_zone_name,
    gz.levelid AS level_id,
    gz.parentid AS parent_id,
    f.id AS facility_id,
    f.code AS facility_code,
    f.name AS facility_name,
    vr.id AS report_id,
    vr.programid,
    e.name AS equipment_name,
    e.model,
    et.name AS equipment_type_name,
    ccli.mintemp,
    ccli.maxtemp,
    ccli.minepisodetemp,
    ccli.maxepisodetemp,
    eet.name AS energy_source,
    es.name AS status
   FROM vaccine_report_cold_chain_line_items ccli
     JOIN vaccine_reports vr ON vr.id = ccli.reportid
     JOIN facilities f ON vr.facilityid = f.id
     JOIN geographic_zones gz ON f.geographiczoneid = gz.id
     JOIN processing_periods pp ON vr.periodid = pp.id
     JOIN equipment_inventories ei ON ei.facilityid = f.id AND ccli.equipmentinventoryid = ei.id
     JOIN equipments e ON ei.equipmentid = e.id
     JOIN equipment_types et ON e.equipmenttypeid = et.id
     JOIN equipment_energy_types eet ON e.energytypeid = eet.id
     LEFT JOIN equipment_inventory_statuses eis ON eis.inventoryid = ei.id
     JOIN equipment_operational_status es ON es.id = eis.statusid;