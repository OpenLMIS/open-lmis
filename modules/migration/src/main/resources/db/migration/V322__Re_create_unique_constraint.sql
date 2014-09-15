drop index uc_processing_period_name_scheduleid;

CREATE UNIQUE INDEX uc_processing_period_name_scheduleid ON processing_periods USING btree (lower(name::text), scheduleid, extract(year from startDate  ));