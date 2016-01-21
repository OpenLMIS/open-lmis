CREATE TABLE facility_program_equipments(
  id                        SERIAL PRIMARY KEY,
  facilityId                INT NOT NULL REFERENCES facilities(id),
  programId                 INT NOT NULL REFERENCES programs(id),
  equipmentId               INT NOT NULL REFERENCES equipments(id),
  operationalStatusId       INT NOT NULL REFERENCES equipment_operational_status(id),
  serialNumber              VARCHAR(200) NOT NULL,
  manufacturerName          VARCHAR(200) NULL,
  model                     VARCHAR(200) NULL,
  energySource              VARCHAR(200) NULL,
  yearOfInstallation        INT NOT NULL DEFAULT 1900,
  purchasePrice             DECIMAL (18,3) NOT NULL DEFAULT 0,
  sourceOfFund              VARCHAR(200) NULL,
  replacementRecommended    BOOLEAN NOT NULL DEFAULT false,
  reasonForReplacement      VARCHAR(2000) NULL,
  nameOfAssessor            VARCHAR(200) NULL,
  dateLastAssessed          DATE NOT NULL DEFAULT NOW(),

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);