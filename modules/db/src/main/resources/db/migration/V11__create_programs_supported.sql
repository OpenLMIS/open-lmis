CREATE TABLE programs_supported (
  facilityId   INTEGER REFERENCES facilities (id),
  programId    INTEGER REFERENCES programs (id),
  startDate    TIMESTAMP NOT NULL,
  active       BOOLEAN NOT NULL,
  modifiedBy   VARCHAR(50),
  modifiedDate TIMESTAMP,
  PRIMARY KEY (facilityId, programId)
);
