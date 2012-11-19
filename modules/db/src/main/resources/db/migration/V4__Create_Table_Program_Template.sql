Drop TABLE IF EXISTS program_rnr_template;
CREATE TABLE program_rnr_template(
    id SERIAL PRIMARY KEY ,
    column_id INTEGER NOT NULL REFERENCES master_rnr_template(id),
    program_id INTEGER NOT NULL,
    label VARCHAR(200) NOT NULL,
    is_used BOOLEAN NOT NULL,
    UNIQUE (program_id, column_id)
);