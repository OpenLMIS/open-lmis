ALTER TABLE facility_demographic_estimates
ADD  createdby     integer ,
ADD  createddate   timestamp DEFAULT NOW(),
ADD  modifiedby    integer,
ADD  modifieddate  timestamp;
