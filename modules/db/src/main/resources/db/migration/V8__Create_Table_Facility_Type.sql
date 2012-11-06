 CREATE TABLE facility_type
(
    id serial primary key,
    facility_type_name varchar(30) not null,
    nominal_max_month integer not null,
    nominal_eop NUMERIC(4,2) not null);










