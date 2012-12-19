CREATE TABLE supervisory_nodes (
  id serial PRIMARY KEY,
  parentId INT REFERENCES supervisory_nodes(id),
  facilityId INT NOT NULL REFERENCES facilities(id),
  name VARCHAR(50) NOT NULL,
  code VARCHAR(50) UNIQUE NOT NULL,
  description VARCHAR(250),
  approvalPoint BOOLEAN NOT NULL,
  modifiedBy VARCHAR(50),
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ucSupervisoryNodeCode ON supervisory_nodes(LOWER(code));
CREATE INDEX iSupervisoryNodeParentId ON supervisory_nodes(parentId);