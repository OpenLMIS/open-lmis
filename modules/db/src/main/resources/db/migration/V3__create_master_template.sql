Drop TABLE IF EXISTS master_rnr_template;
CREATE TABLE master_rnr_template (
    id serial PRIMARY KEY,
    column_name varchar(200) NOT NULL UNIQUE,
    column_position integer  NOT NULL,
    source VARCHAR(1) NOT NULL,
    is_source_configurable boolean NOT NULL,
    column_label varchar(200),
    formula varchar(200),
    column_indicator varchar(3) not null,
    is_used boolean NOT NULL,
    is_visible boolean NOT NULL,
    is_mandatory boolean NOT NULL,
    description varchar(250)
);