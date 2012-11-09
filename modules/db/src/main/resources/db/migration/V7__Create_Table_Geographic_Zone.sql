CREATE TABLE geographic_zone(
  id SERIAL PRIMARY KEY,
  name VARCHAR(30) NOT NULL,
  level INTEGER NOT NULL REFERENCES geopolitical_level(id),
  parent INTEGER REFERENCES geographic_zone(id)
);