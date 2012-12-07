CREATE TABLE requisition_group_members (
  id serial PRIMARY KEY,
  requisition_group_id INT NOT NULL REFERENCES requisition_group(id),
  facility_id INT NOT NULL REFERENCES facility(id),
  modified_by VARCHAR(50),
  modified_date TIMESTAMP  DEFAULT  CURRENT_TIMESTAMP,
  UNIQUE (requisition_group_id, facility_id)
);