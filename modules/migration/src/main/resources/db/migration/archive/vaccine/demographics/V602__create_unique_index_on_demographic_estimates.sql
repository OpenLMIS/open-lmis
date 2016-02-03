CREATE UNIQUE INDEX unique_index_facility_demographic_estimates
  ON facility_demographic_estimates (facilityId, year, programId, demographicEstimateId);

CREATE UNIQUE INDEX unique_index_district_demographic_estimates
  ON district_demographic_estimates (districtId, year, programId, demographicEstimateId);

