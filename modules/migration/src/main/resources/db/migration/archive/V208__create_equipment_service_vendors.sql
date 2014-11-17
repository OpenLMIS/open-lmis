CREATE TABLE equipment_service_vendors(
  id                        SERIAL PRIMARY KEY ,
  name                      VARCHAR (1000) NOT NULL,
  website                   VARCHAR (1000) NOT NULL,
  contactPerson             VARCHAR (200),
  primaryPhone              VARCHAR (20),
  email                     VARCHAR (200),
  description               VARCHAR (2000),
  specialization            VARCHAR (2000),
  geographicCoverage        VARCHAR (2000),
  registrationDate          DATE,

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);