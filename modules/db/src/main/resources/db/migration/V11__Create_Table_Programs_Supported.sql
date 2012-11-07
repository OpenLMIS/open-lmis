CREATE TABLE PROGRAMS_SUPPORTED
(
    id serial primary key,
    facility_code char(6) references facility(facility_code) not null,
    program_id integer references program(id) not null,
    active boolean not null);
