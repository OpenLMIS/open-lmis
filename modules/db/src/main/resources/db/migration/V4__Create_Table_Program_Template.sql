Drop TABLE IF EXISTS Program_RnR_Template;
CREATE TABLE Program_RnR_Template(
    id serial PRIMARY KEY ,
    column_id integer NOT NULL references Master_RnR_Template(id),
    program_id integer NOT NULL,
    is_used boolean not null,
    UNIQUE (program_id, column_id)
);