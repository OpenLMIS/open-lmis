CREATE UNIQUE INDEX unique_donor_code_index
ON donors (code);

CREATE UNIQUE INDEX unique_program_equipment_index
ON program_equipments (programid, equipmentid);

CREATE UNIQUE INDEX unique_program_equipment_product_index
ON program_equipment_products (programequipmentid, productid);

CREATE UNIQUE INDEX unique_equipment_type_code_index
ON equipment_types (code);

CREATE UNIQUE INDEX unique_equipment_code
ON equipments (code);