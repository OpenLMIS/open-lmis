CREATE TABLE facility_type (
  id INTEGER PRIMARY KEY,
  name VARCHAR(30) NOT NULL UNIQUE,
  description varchar(250) ,
  level_id INTEGER,
  is_active BOOLEAN,
  nominal_max_month INTEGER NOT NULL,
  nominal_eop NUMERIC(4,2) NOT NULL,
  modified_date TIMESTAMP DEFAULT  CURRENT_TIMESTAMP
);










