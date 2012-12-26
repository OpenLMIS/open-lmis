Drop TABLE IF EXISTS program_rnr_columns;
CREATE TABLE program_rnr_columns(
    id SERIAL PRIMARY KEY ,
    masterColumnId INTEGER NOT NULL REFERENCES master_rnr_columns(id),
    programId INTEGER NOT NULL,
    label VARCHAR(200) NOT NULL,
    visible BOOLEAN NOT NULL,
    position int NOT NULL,
    source VARCHAR(1),
    formulaValidated BOOLEAN,
    UNIQUE (programId, masterColumnId)
);