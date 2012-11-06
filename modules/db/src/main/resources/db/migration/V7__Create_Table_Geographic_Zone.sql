CREATE TABLE geographic_zone
(
    id serial primary key,
    zone_name varchar(30) not null,
    zone_level integer not null references geopolitical_level(id),
    parent integer references geographic_zone(id) );