CREATE TABLE facility_type (
  code VARCHAR(50) PRIMARY KEY,
  name VARCHAR(30) NOT NULL UNIQUE,
  description varchar(250) ,
  level_id INTEGER,
  nominal_max_month INTEGER NOT NULL,
  nominal_eop NUMERIC(4,2) NOT NULL,
  display_order INTEGER,
  is_active BOOLEAN,
  last_modified_date TIMESTAMP DEFAULT  CURRENT_TIMESTAMP
);