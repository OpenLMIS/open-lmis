CREATE TABLE facility (
    code VARCHAR(6) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type INTEGER NOT NULL REFERENCES facility_type(id),
    geographic_zone_id INTEGER NOT NULL REFERENCES geographic_zone(id)
);