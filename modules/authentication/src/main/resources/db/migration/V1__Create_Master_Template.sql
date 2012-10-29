CREATE TABLE Master_Program_Template(
    id serial PRIMARY KEY ,
    description varchar(100),
    field_name varchar(50) NOT NULL,
    field_position integer  NOT NULL,
    field_label varchar(50),
    default_value varchar(50) NOT null ,
    data_source varchar(50) not null,
    formula varchar,
    field_indicator char(3) not null,
    isUsed boolean not null,
    isVisible  boolean not null
);

