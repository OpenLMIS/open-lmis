CREATE TABLE program (
    id integer primary key,
    code varchar(50) UNIQUE,
    name varchar(50),
    description varchar(50),
    budgeting_applies boolean,
    uses_dar boolean,
    active boolean,
    last_modified_date date,
    last_modified_by integer
);