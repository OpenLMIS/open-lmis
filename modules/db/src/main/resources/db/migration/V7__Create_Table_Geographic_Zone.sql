CREATE TABLE geographic_zone(
  id SERIAL PRIMARY KEY,
  zone_name VARCHAR(30) NOT NULL,
  zone_level INTEGER NOT NULL REFERENCES geopolitical_level(id),
  parent INTEGER REFERENCES geographic_zone(id)
);