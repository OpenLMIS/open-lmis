CREATE TABLE program_equipments(
  id                        SERIAL PRIMARY KEY,
  programId                 INT NOT NULL REFERENCES programs(id),
  equipmentId               INT NOT NULL REFERENCES equipments(id),
  displayOrder              INT NOT NULL,
  enableTestCount           BOOLEAN DEFAULT false,
  enableTotalColumn         BOOLEAN DEFAULT false,

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);