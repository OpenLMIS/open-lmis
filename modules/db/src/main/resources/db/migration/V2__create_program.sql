CREATE TABLE program (
    id SERIAL primary key,
    code varchar(50) not null unique,
    name varchar(50),
    description varchar(50),
    budgeting_applies boolean,
    uses_dar boolean,
    active boolean,
    last_modified_date date,
    last_modified_by integer
);