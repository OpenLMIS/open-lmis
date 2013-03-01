CREATE TABLE programs_supported (
  facilityId   INTEGER REFERENCES facilities (id),
  programId    INTEGER REFERENCES programs (id),
  startDate    TIMESTAMP,
  active       BOOLEAN NOT NULL,
  modifiedBy   INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (facilityId, programId)
);
