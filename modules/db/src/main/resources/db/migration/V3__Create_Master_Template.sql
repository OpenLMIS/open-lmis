Drop TABLE IF EXISTS Master_Program_Template;
CREATE TABLE Master_RnR_Template (
    id serial PRIMARY KEY ,
    description varchar(100),
    column_name varchar(50) NOT NULL,
    column_position integer  NOT NULL,
    column_label varchar(50),
    default_value varchar(50) NOT null ,
    data_source varchar(50) not null,
    formula varchar,
    column_indicator varchar(3) not null,
    is_used boolean not null,
    is_visible boolean not null
);