ALTER TABLE facility_program_equipments
RENAME TO equipment_inventories;

ALTER TABLE program_equipments
RENAME TO equipment_programs;

ALTER TABLE program_equipment_products
RENAME TO equipment_products;