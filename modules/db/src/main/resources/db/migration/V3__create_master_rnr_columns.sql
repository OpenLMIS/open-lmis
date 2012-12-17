Drop TABLE IF EXISTS master_rnr_columns;
CREATE TABLE master_rnr_columns (
    id serial PRIMARY KEY,
    name varchar(200) NOT NULL UNIQUE,
    position integer  NOT NULL,
    source VARCHAR(1) NOT NULL,
    sourceConfigurable boolean NOT NULL,
    label varchar(200),
    formula varchar(200),
    indicator varchar(3) not null,
    used boolean NOT NULL,
    visible boolean NOT NULL,
    mandatory boolean NOT NULL,
    description varchar(250)
);