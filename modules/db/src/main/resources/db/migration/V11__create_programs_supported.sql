CREATE TABLE PROGRAMS_SUPPORTED (
    facility_id INTEGER REFERENCES facility(id),
    program_code VARCHAR(50) REFERENCES program(code),
    active BOOLEAN NOT NULL,
    modified_by varchar(50),
    modified_date TIMESTAMP,
    PRIMARY KEY (facility_id, program_code)
);
