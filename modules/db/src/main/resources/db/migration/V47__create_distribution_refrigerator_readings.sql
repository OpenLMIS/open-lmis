DROP TABLE IF EXISTS distribution_refrigerator_readings;
CREATE TYPE radio_options AS ENUM ('Y', 'N', 'D');

CREATE TABLE distribution_refrigerator_readings (
  id                   SERIAL PRIMARY KEY,
  temperature          NUMERIC(3, 1),
  functioningCorrectly radio_options,
  lowAlarmEvents       INTEGER,
  highAlarmEvents      INTEGER,
  problemSinceLastTime radio_options,
  problemList          VARCHAR,
  notes                VARCHAR(30),
  refrigeratorId       INTEGER REFERENCES refrigerators (id),
  distributionId       INTEGER REFERENCES distributions (id),
  createdBy            INTEGER NOT NULL REFERENCES users (id),
  createdDate          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy           INTEGER NOT NULL REFERENCES users (id),
  modifiedDate         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uc_refrigerator_distribution  UNIQUE(refrigeratorId, distributionId)
);

