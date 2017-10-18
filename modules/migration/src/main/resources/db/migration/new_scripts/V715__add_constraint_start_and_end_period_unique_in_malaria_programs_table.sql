ALTER TABLE malaria_programs ADD CONSTRAINT start_and_end_period_unique UNIQUE (periodstartdate, periodenddate);
