CREATE TABLE programs_supported (
  facilityId   INTEGER REFERENCES facilities (id),
  programId    INTEGER REFERENCES programs (id),
  startDate    TIMESTAMP,
  active       BOOLEAN NOT NULL,
  modifiedBy   INTEGER,
  modifiedDate TIMESTAMP,
  createdBy INTEGER,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  PRIMARY KEY (facilityId, programId)
);
