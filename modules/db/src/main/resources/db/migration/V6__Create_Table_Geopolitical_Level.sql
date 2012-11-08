CREATE TABLE geopolitical_level(
    id SERIAL primary key,
    level_number INTEGER NOT NULL UNIQUE ,
    level_name VARCHAR(30) NOT NULL
);