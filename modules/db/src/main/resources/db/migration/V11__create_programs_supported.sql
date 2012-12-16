CREATE TABLE PROGRAMS_SUPPORTED (
    facilityId INTEGER REFERENCES facilities(id),
    programId INTEGER REFERENCES program(id),
    active BOOLEAN NOT NULL,
    modifiedBy varchar(50),
    modifiedDate TIMESTAMP,
    PRIMARY KEY (facilityId, programId)
);
