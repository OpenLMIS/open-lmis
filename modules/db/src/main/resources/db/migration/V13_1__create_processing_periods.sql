CREATE TABLE processing_periods (
  id SERIAL PRIMARY KEY,
  scheduleId INTEGER REFERENCES processing_schedules(id) NOT NULL,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(250),
  startDate TIMESTAMP NOT NULL,
  endDate TIMESTAMP NOT NULL,
  numberOfMonths INTEGER,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE UNIQUE INDEX uc_period_name ON processing_periods(LOWER(name), scheduleId);