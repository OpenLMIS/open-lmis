CREATE TABLE facility (
    facility_code VARCHAR(6) PRIMARY KEY,
    facility_name VARCHAR(50) NOT NULL,
    facility_type INTEGER NOT NULL REFERENCES facility_type(id),
    geographic_zone_id INTEGER NOT NULL REFERENCES geographic_zone(id)
);