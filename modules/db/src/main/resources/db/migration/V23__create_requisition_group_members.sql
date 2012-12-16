CREATE TABLE requisition_group_members (
  requisition_group_id INT NOT NULL REFERENCES requisition_group(id),
  facility_id INT NOT NULL REFERENCES facilities(id),
  modified_by VARCHAR(50),
  modified_date TIMESTAMP  DEFAULT  CURRENT_TIMESTAMP,
  PRIMARY KEY (requisition_group_id, facility_id)
);