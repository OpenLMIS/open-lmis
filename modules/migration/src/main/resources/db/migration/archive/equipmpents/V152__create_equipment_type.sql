
CREATE TABLE equipment_types(
  id                        SERIAL PRIMARY KEY,
  code                      VARCHAR(20) NOT NULL UNIQUE,
  name                      VARCHAR(200),
  displayOrder              INT NOT NULL DEFAULT 0,

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
