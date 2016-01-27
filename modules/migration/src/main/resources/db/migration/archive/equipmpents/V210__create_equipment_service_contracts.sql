CREATE TABLE equipment_service_contracts(
  id                        SERIAL PRIMARY KEY,
  vendorId                  INTEGER NOT NULL REFERENCES equipment_service_vendors(id),
  identifier                VARCHAR (1000) NOT NULL,
  startDate                 DATE,
  endDate                   DATE,
  description               VARCHAR (2000),
  terms                     VARCHAR (2000),
  coverage                  VARCHAR (2000),
  contractDate              DATE,

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);