CREATE OR REPLACE VIEW vw_cce_repair_management_not_functional AS
 SELECT notfunctional.facilityid AS fid,
    notfunctional.name AS operationalstatus,
    count(1) OVER (PARTITION BY notfunctional.notfunctionalstatusid, notfunctional.facilityid) AS notfunctionalstatuscount
   FROM ( SELECT DISTINCT ON (eis.inventoryid) eis.inventoryid,
            eis.notfunctionalstatusid,
            eos.name AS status,
            eosnf.name,
            ei.facilityid,
            ei.programid
           FROM equipment_inventory_statuses eis
             LEFT JOIN equipment_operational_status eosnf ON eosnf.id = eis.notfunctionalstatusid
             LEFT JOIN equipment_operational_status eos ON eos.id = eis.statusid
             LEFT JOIN equipment_inventories ei ON ei.id = eis.inventoryid
             JOIN equipments e ON ei.equipmentid = e.id
             JOIN equipment_types et ON e.equipmenttypeid = et.id
             JOIN facilities f ON f.id = ei.facilityid
          WHERE et.iscoldchain IS TRUE
          ORDER BY eis.inventoryid, eis.createddate DESC) notfunctional;