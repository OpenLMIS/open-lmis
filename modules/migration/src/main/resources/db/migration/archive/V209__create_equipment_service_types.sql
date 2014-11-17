CREATE TABLE equipment_service_types(
  id                        SERIAL PRIMARY KEY ,
  name                      VARCHAR (1000) NOT NULL,
  description               VARCHAR (2000) NOT NULL,

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);