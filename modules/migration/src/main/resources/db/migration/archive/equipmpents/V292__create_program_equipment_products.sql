CREATE TABLE program_equipment_products(
  id                        SERIAL PRIMARY KEY,
  programEquipmentId        INT NOT NULL REFERENCES program_equipments(id),
  productId                 INT NOT NULL REFERENCES products(id),
  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);