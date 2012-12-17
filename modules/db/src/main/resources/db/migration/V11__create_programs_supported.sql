CREATE TABLE PROGRAMS_SUPPORTED (
    facilityId INTEGER REFERENCES facilities(id),
    programId INTEGER REFERENCES programs(id),
    active BOOLEAN NOT NULL,
    modifiedBy varchar(50),
    modifiedDate TIMESTAMP,
    PRIMARY KEY (facilityId, programId)
);
