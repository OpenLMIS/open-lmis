ALTER TABLE equipments
  ADD COLUMN manufacturer character varying(200);
ALTER TABLE equipments
  ADD COLUMN model character varying(200);
ALTER TABLE equipments
  ADD COLUMN energytypeid integer;

ALTER TABLE equipment_cold_chain_equipment_energy_types
  RENAME TO equipment_energy_types;

ALTER TABLE equipments
  ADD CONSTRAINT equipment_energy_type_fkey FOREIGN KEY (energytypeid) REFERENCES equipment_energy_types (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE equipment_cold_chain_equipments
  DROP CONSTRAINT equipment_cce_pkey;
ALTER TABLE equipment_cold_chain_equipments
  DROP COLUMN id;
ALTER TABLE equipment_cold_chain_equipments
  DROP COLUMN brand;
ALTER TABLE equipment_cold_chain_equipments
  DROP COLUMN model;
ALTER TABLE equipment_cold_chain_equipments
  DROP CONSTRAINT equipment_cce_energy_type_fkey;
ALTER TABLE equipment_cold_chain_equipments
  DROP COLUMN energytypeid;
ALTER TABLE equipment_cold_chain_equipments
  ADD CONSTRAINT equipment_cce_pkey PRIMARY KEY (equipmentid);

ALTER TABLE equipments ALTER COLUMN equipmenttypeid DROP NOT NULL;
UPDATE equipments SET equipmenttypeid=null;
DELETE FROM equipment_types;
INSERT INTO equipment_types SELECT id, code, name FROM equipments;
UPDATE equipments set equipmenttypeid=eqt.id FROM (select id,code from equipment_types) AS eqt
WHERE equipments.code=eqt.code;
ALTER TABLE equipments ALTER COLUMN equipmenttypeid SET NOT NULL;

ALTER TABLE equipment_programs
  DROP CONSTRAINT program_equipments_equipmentid_fkey;
ALTER TABLE equipment_programs RENAME equipmentid  TO equipmenttypeid;
ALTER TABLE equipment_programs
  ADD CONSTRAINT program_equipments_equipmenttypeid FOREIGN KEY (equipmenttypeid) REFERENCES equipment_types (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE equipment_programs
  RENAME TO equipment_type_programs;

ALTER TABLE equipment_products RENAME programequipmentid  TO programequipmenttypeid;
ALTER TABLE equipment_products
  RENAME TO equipment_type_products;

ALTER TABLE equipment_service_contract_equipments
  DROP CONSTRAINT equipment_service_contract_equipments_equipmentid_fkey;
ALTER TABLE equipment_service_contract_equipments RENAME equipmentid  TO equipmenttypeid;
ALTER TABLE equipment_service_contract_equipments
  ADD CONSTRAINT equipment_service_contract_equipments_equipmenttypeid_fkey FOREIGN KEY (equipmenttypeid) REFERENCES equipment_types (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE equipment_service_contract_equipments
  RENAME TO equipment_service_contract_equipment_types;

