CREATE TABLE demographic_estimates
(
  id SERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL UNIQUE,
  isPrimaryEstimate BOOLEAN NOT NULL DEFAULT(FALSE),
  defaultConversionFactor DECIMAL NOT NULL DEFAULT (1)
);

CREATE TABLE facility_demographic_estimates
(
  id SERIAL PRIMARY KEY,
  year INT NOT NULL,
  facilityId INT NOT NULL REFERENCES facilities(id),
  demographicEstimateId INT NOT NULL REFERENCES demographic_estimates(id),
  conversionFactor DECIMAL NULL,
  value INT NOT NULL DEFAULT (0)
);

INSERT INTO demographic_estimates
(name, isPrimaryEstimate, defaultConversionFactor)
    VALUES
      ('Population', true, 1)