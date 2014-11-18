CREATE TABLE equipment_maintenance_requests(
  id                        SERIAL PRIMARY KEY ,
  userId                    INTEGER NOT NULL REFERENCES users(id),
  facilityId                INTEGER NOT NULL REFERENCES facilities(id),
  inventoryId               INTEGER NOT NULL REFERENCES facility_program_equipments (id),
  vendorId                  INTEGER NULL REFERENCES equipment_service_vendors(id),

  requestDate               DATE,
  reason                    VARCHAR (2000),
  recommendedDate           DATE,
  comment                   VARCHAR(2000),

  resolved                  BOOLEAN NOT NULL DEFAULT (false),
  vendorComment             VARCHAR(2000),

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);