CREATE TABLE equipments(
  id                        SERIAL PRIMARY KEY,
  code                      VARCHAR(200) NOT NULL,
  name                      VARCHAR(200) NOT NULL,
  equipmentType             INT NOT NULL REFERENCES equipment_types(id),

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);