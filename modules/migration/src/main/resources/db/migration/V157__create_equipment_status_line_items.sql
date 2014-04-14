CREATE TABLE equipment_status_line_items(
  id                        SERIAL PRIMARY KEY,
  facilityId                INT NOT NULL REFERENCES facilities(id),
  programId                 INT NOT NULL REFERENCES programs(id),
  periodId                  INT NOT NULL REFERENCES processing_periods(id),
  operationalStatusId       INT NOT NULL REFERENCES equipment_operational_status(id),
  testCount                 INT NULL,
  totalCount                INT NULL,
  daysOutOfUse              INT NOT NULL,
  remarks                   VARCHAR (200) NULL,

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);