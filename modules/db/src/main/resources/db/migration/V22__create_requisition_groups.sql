CREATE TABLE requisition_groups (
  id serial PRIMARY KEY,
  code varchar(50) UNIQUE,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(250),
  supervisoryNodeId INTEGER,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ucRequisitionGroupCode ON requisition_groups(LOWER(code));