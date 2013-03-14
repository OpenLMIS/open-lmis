CREATE TABLE geographic_levels (
    id            SERIAL PRIMARY KEY,
    code          VARCHAR(50) NOT NULL UNIQUE,
    name          VARCHAR(250) NOT NULL,
    level         INTEGER NOT NULL
);