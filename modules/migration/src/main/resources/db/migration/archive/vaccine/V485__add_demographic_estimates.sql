ALTER TABLE demographic_estimate_categories
ADD isPrimaryEstimate BOOLEAN NOT NULL DEFAULT(FALSE),
ADD  defaultConversionFactor DECIMAL NOT NULL DEFAULT (1);

CREATE TABLE facility_demographic_estimates
(
  id SERIAL PRIMARY KEY,
  year INT NOT NULL,
  facilityId INT NOT NULL REFERENCES facilities(id),
  demographicEstimateId INT NOT NULL REFERENCES demographic_estimate_categories(id),
  conversionFactor DECIMAL NULL,
  value INT NOT NULL DEFAULT (0)
);

-- insert application, required estimate category
INSERT INTO demographic_estimate_categories
(name, isPrimaryEstimate, defaultConversionFactor)
    VALUES
      ('Population', true, 1);