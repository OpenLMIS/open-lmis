CREATE TABLE equipment_operational_status(
  id                        SERIAL PRIMARY KEY,
  name                      VARCHAR(200) NOT NULL,
  displayOrder              INT NOT NULL DEFAULT 0,

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);