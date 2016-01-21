ALTER TABLE facility_demographic_estimates
  ADD programId       INT NOT NULL references programs(id),
  ADD isFinal         BOOLEAN NOT NULL default(false);

ALTER TABLE district_demographic_estimates
  ADD programId       INT NOT NULL references programs(id),
  ADD isFinal         BOOLEAN NOT NULL default(false);
