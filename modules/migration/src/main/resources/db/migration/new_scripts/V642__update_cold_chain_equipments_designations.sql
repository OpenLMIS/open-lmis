DO $$
BEGIN

ALTER TABLE equipment_cold_chain_equipment_designations
  ADD COLUMN "hasEnergy" boolean DEFAULT TRUE;
ALTER TABLE equipment_cold_chain_equipment_designations
  ADD COLUMN "isRefrigerator" boolean;
ALTER TABLE equipment_cold_chain_equipment_designations
  ADD COLUMN "isFreezer" boolean;

ALTER TABLE equipment_cold_chain_equipments
ADD COLUMN "capacity" numeric(8,2);

update equipment_cold_chain_equipment_designations set "isRefrigerator"=true, "isFreezer"=true where name='Refrigerator & Freezer';
update equipment_cold_chain_equipment_designations set "isRefrigerator"=true, "isFreezer"=false where name='Refrigerator';
update equipment_cold_chain_equipment_designations set "isRefrigerator"=false, "isFreezer"=true where name='Freezer';
insert into equipment_cold_chain_equipment_designations("name","hasEnergy","isRefrigerator","isFreezer") Values('Cold Box',false,false,false);

DROP VIEW IF EXISTS vw_cce_repair_management_not_functional;
CREATE OR REPLACE VIEW vw_cce_repair_management_not_functional AS
 SELECT notfunctional.facilityid AS facilityId,
        notfunctional.facilityid AS fid,
	notfunctional.pid AS programId,
	notfunctional.model AS model,
	notfunctional.name AS operationalstatus,
	notfunctional.modifieddate AS modifieddate,
	notfunctional.modifiedby AS modifiedby,
	notfunctional.fname AS facilityname,
    count(1) OVER (PARTITION BY notfunctional.notfunctionalstatusid, notfunctional.facilityid) AS notfunctionalstatuscount
   FROM ( SELECT DISTINCT ON (eis.inventoryid) eis.inventoryid,
            eis.notfunctionalstatusid,
            eos.name AS status,
            eosnf.name,
            ei.facilityid,
            f.name as fname,
            ei.programid as pid,
            e.model as model,
            ei.modifieddate as modifieddate,
            ei.modifiedby as modifiedby
           FROM equipment_inventory_statuses eis
             LEFT JOIN equipment_operational_status eosnf ON eosnf.id = eis.notfunctionalstatusid
             LEFT JOIN equipment_operational_status eos ON eos.id = eis.statusid
             LEFT JOIN equipment_inventories ei ON ei.id = eis.inventoryid
             JOIN equipments e ON ei.equipmentid = e.id
             JOIN equipment_types et ON e.equipmenttypeid = et.id
             JOIN facilities f ON f.id = ei.facilityid
          WHERE et.iscoldchain IS TRUE
          ORDER BY eis.inventoryid, eis.createddate DESC) notfunctional WHERE name IS NOT NULL;

ALTER TABLE vw_cce_repair_management_not_functional
  OWNER TO postgres;
END;
$$