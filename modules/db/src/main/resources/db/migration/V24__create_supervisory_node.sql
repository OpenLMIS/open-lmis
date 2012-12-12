CREATE TABLE supervisory_node (
  id serial PRIMARY KEY,
  parent_id INT REFERENCES supervisory_node(id),
  facility_id INT NOT NULL REFERENCES facility(id),
  name VARCHAR(50) NOT NULL,
  code VARCHAR(50) UNIQUE NOT NULL,
  description VARCHAR(250),
  approval_point BOOLEAN NOT NULL,
  modified_by VARCHAR(50),
  modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uc_supervisory_node_code ON supervisory_node(LOWER(code));