CREATE TABLE geographic_zones (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(250) NOT NULL,
  levelId INTEGER NOT NULL REFERENCES geographic_levels(id),
  parent INTEGER REFERENCES geographic_zones(id),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP,
  createdBy INTEGER,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);