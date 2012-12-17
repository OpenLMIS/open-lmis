Drop TABLE IF EXISTS program_rnr_template;
CREATE TABLE program_rnr_template(
    id SERIAL PRIMARY KEY ,
    column_id INTEGER NOT NULL REFERENCES master_rnr_columns(id),
    program_code VARCHAR(50) NOT NULL,
    label VARCHAR(200) NOT NULL,
    is_visible BOOLEAN NOT NULL,
    position int NOT NULL,
    source VARCHAR(1),
    UNIQUE (program_code, column_id)
);