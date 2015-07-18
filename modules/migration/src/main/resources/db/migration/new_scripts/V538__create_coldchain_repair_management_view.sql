CREATE OR REPLACE VIEW vw_cce_repair_management AS
 SELECT ei.id,
    ei.programid AS pid,
    ei.facilityid AS fid,
    f.geographiczoneid AS geoid,
    ft.code AS facility_code,
    f.name AS facility_name,
    e.model,
    et.name AS type_name,
    eos.name AS operationalstatus,
    count(1) OVER (PARTITION BY eos.name, ei.facilityid) AS operationalstatuscount,
    eet.name AS energytype,
    count(1) OVER (PARTITION BY eet.name, ei.facilityid) AS energytypecount
   FROM equipment_inventories ei
     JOIN equipment_inventory_statuses eis ON eis.id = (( SELECT eisb.id
           FROM equipment_inventory_statuses eisb
          WHERE eisb.inventoryid = ei.id
          ORDER BY eisb.createddate DESC
         LIMIT 1))
     JOIN equipment_operational_status eos ON eis.statusid = eos.id
     JOIN equipments e ON ei.equipmentid = e.id
     JOIN equipment_types et ON e.equipmenttypeid = et.id
     JOIN facilities f ON f.id = ei.facilityid
     JOIN facility_types ft ON ft.id = f.typeid
     LEFT JOIN equipment_energy_types eet ON e.energytypeid = eet.id
  WHERE et.iscoldchain IS TRUE;