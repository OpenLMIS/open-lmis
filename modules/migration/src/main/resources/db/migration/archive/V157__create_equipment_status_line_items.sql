DROP TABLE IF EXISTS equipment_status_line_items;

CREATE TABLE equipment_status_line_items(
  id                        SERIAL PRIMARY KEY,

  rnrId                     INT NOT NULL REFERENCES requisitions(id),
  code                      VARCHAR(200) NOT NULL,
  equipmentName             VARCHAR(200) NOT NULL,
  equipmentCategory         VARCHAR(200) NOT NULL,
  equipmentModel            VARCHAR(200) NULL,
  equipmentSerial           VARCHAR(200) NULL,
  equipmentInventoryId      INT NOT NULL REFERENCES facility_program_equipments(id),

  operationalStatusId       INT NOT NULL REFERENCES equipment_operational_status(id),
  testCount                 INT NULL,
  totalCount                INT NULL,
  daysOutOfUse              INT NOT NULL,
  remarks                   VARCHAR (2000) NULL,

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);