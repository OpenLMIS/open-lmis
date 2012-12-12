CREATE TABLE requisition_group (
  id serial PRIMARY KEY,
  code varchar(50) UNIQUE,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(250),
  supervisory_node_id INTEGER,

  modified_by VARCHAR(50),
  modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uc_requisition_group_code ON requisition_group(LOWER(code));