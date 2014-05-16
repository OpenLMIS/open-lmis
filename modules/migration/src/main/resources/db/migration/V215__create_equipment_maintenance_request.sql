CREATE TABLE equipment_maintenance_requests(
  id                        SERIAL PRIMARY KEY ,
  userId                    INTEGER NOT NULL REFERENCES users(id),
  vendorId                  INTEGER NOT NULL REFERENCES equipment_service_vendors(id),
  requestDate               DATE,
  reason                    VARCHAR (2000),
  recommendedDate           DATE,
  comment                   VARCHAR(2000),

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);