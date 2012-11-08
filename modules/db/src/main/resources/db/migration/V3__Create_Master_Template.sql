Drop TABLE IF EXISTS master_program_template;
CREATE TABLE master_rnr_template (
    id SERIAL PRIMARY KEY ,
    description VARCHAR(100),
    column_name VARCHAR(50) NOT NULL,
    column_position INTEGER  NOT NULL,
    column_label VARCHAR(50),
    default_value VARCHAR(50) NOT NULL ,
    data_source VARCHAR(50) NOT NULL,
    formula VARCHAR,
    column_indicator VARCHAR(3) NOT NULL,
    is_used boolean NOT NULL,
    is_visible boolean NOT NULL
);