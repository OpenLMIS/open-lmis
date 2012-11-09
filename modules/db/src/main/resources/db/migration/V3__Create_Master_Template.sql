Drop TABLE IF EXISTS Master_Program_Template;
CREATE TABLE Master_RnR_Template (
    id serial PRIMARY KEY,
    description varchar(250),
    column_name varchar(200) NOT NULL,
    column_position integer  NOT NULL,
    column_label varchar(200),
    default_value varchar(50) ,
    data_source varchar(50) not null,
    formula varchar(200),
    column_indicator varchar(3) not null,
    is_used boolean not null,
    is_visible boolean not null,
    is_mandatory boolean not null
);