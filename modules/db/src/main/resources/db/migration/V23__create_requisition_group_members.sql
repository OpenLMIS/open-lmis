CREATE TABLE requisition_group_members (
  requisitionGroupId INT NOT NULL REFERENCES requisition_groups(id),
  facilityId INT NOT NULL REFERENCES facilities(id),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  createdBy INTEGER,
  PRIMARY KEY (requisitionGroupId, facilityId)
);