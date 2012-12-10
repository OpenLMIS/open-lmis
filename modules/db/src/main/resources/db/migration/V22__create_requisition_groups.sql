CREATE TABLE requisition_group (
  id serial PRIMARY KEY,
  code varchar(50) UNIQUE,
  name VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(50),
  level_id VARCHAR(10),
  head_facility_id INT REFERENCES facility(id) NOT NULL,
  parent_id int REFERENCES requisition_group(id),
  active BOOLEAN,

  modified_by VARCHAR(50),
  modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uc_requisition_group_code ON requisition_group(LOWER(code));