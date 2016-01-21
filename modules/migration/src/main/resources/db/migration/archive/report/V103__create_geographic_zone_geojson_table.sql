CREATE TABLE geographic_zone_geojson(
  id SERIAL PRIMARY KEY,
  zoneId INTEGER,
  geoJsonId INTEGER,
  geometry TEXT,
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);