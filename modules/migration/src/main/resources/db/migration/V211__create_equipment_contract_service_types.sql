CREATE TABLE equipment_contract_service_types(
  id                        SERIAL PRIMARY KEY ,
  contractId                INTEGER REFERENCES equipment_service_contracts(id),
  serviceTypeId             INTEGER REFERENCES equipment_service_types(id),

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);