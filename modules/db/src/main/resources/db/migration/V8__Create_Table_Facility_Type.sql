CREATE TABLE facility_type (
  id SERIAL PRIMARY KEY,
  facility_type_name VARCHAR(30) NOT NULL,
  nominal_max_month INTEGER NOT NULL,
  nominal_eop NUMERIC(4,2) NOT NULL
);










