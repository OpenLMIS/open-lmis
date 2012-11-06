CREATE TABLE FACILITY_PROGRAM
(
    id serial primary key,
    facility_id integer references facility(id) not null,
    program_id integer references program(id) not null,
    active boolean not null);
