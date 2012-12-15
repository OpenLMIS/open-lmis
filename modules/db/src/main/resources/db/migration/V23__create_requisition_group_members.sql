CREATE TABLE requisition_group_members (
  requisitionGroupId INT NOT NULL REFERENCES requisition_groups(id),
  facilityId INT NOT NULL REFERENCES facilities(id),
  modifiedBy VARCHAR(50),
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (requisitionGroupId, facilityId)
);