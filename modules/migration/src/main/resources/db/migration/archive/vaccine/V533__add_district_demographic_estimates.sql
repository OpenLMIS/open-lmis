CREATE TABLE district_demographic_estimates
(
  id                SERIAL  PRIMARY KEY,
  year              INT NOT NULL,
  districtId        INT NOT NULL REFERENCES geographic_zones(id),
  demographicEstimateId INT NOT NULL REFERENCES demographic_estimate_categories(id),
  conversionFactor  DECIMAL NULL,
  value             INT NOT NULL DEFAULT (0),

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);