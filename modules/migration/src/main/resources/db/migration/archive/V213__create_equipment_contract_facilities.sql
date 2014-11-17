CREATE TABLE equipment_service_contract_facilities(
  id                        SERIAL PRIMARY KEY ,
  contractId                INTEGER REFERENCES equipment_service_contracts (id),
  facilityId                INTEGER REFERENCES facilities(id),

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);