CREATE TABLE geopolitical_level
(
    id serial primary key,
    level_number integer not null unique ,
    level_name varchar(30) not null);