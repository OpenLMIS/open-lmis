CREATE TABLE PROGRAMS_SUPPORTED (
    id SERIAL PRIMARY KEY,
    facility_code VARCHAR(6) REFERENCES facility(code),
    program_code VARCHAR(50) REFERENCES program(code),
    active BOOLEAN NOT NULL
);
