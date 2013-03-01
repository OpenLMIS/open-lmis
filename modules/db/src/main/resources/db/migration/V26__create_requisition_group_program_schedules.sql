CREATE TABLE requisition_group_program_schedules (
  requisitionGroupId INTEGER REFERENCES requisition_groups(id),
  programId INTEGER REFERENCES programs(id),
  scheduleId INTEGER REFERENCES processing_schedules(id),
  directDelivery BOOLEAN NOT NULL,
  dropOffFacilityId INTEGER REFERENCES facilities(id),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (requisitionGroupId, programId)
);