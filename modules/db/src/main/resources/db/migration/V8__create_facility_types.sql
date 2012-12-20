CREATE TABLE facility_types (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(30) NOT NULL UNIQUE,
  description varchar(250) ,
  levelId INTEGER,
  nominalMaxMonth INTEGER NOT NULL,
  nominalEop NUMERIC(4,2) NOT NULL,
  displayOrder INTEGER,
  active BOOLEAN,
  lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);