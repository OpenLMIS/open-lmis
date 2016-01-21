CREATE TABLE equipment_service_contract_equipments(
  id                        SERIAL PRIMARY KEY,
  contractId                INTEGER NOT NULL REFERENCES equipment_service_contracts(id),
  equipmentId               INTEGER NOT NULL REFERENCES equipments (id),

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);