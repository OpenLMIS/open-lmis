CREATE TABLE facility
(
    facility_code char(6) primary key,
    facility_name varchar(50) not null,
    facility_type integer not null,
    geographic_zone_id integer not null
);