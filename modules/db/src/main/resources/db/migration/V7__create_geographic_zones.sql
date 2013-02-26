CREATE TABLE geographic_zones (
  id INTEGER PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(250) NOT NULL,
  level INTEGER NOT NULL REFERENCES geographic_levels(id),
  parent INTEGER REFERENCES geographic_zones(id),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT  CURRENT_TIMESTAMP
);