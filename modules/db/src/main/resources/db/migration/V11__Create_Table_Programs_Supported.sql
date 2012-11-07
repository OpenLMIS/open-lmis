CREATE TABLE PROGRAMS_SUPPORTED
(
    id serial primary key,
    facility_code char(6) references facility(facility_code),
    program_id integer references program(id),
    active boolean not null
);
