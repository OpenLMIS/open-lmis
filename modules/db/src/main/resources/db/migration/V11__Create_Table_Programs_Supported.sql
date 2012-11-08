CREATE TABLE PROGRAMS_SUPPORTED (
    id SERIAL PRIMARY KEY,
    facility_code VARCHAR(6) REFERENCES facility(facility_code),
    program_id INTEGER REFERENCES program(id),
    active BOOLEAN NOT NULL
);
