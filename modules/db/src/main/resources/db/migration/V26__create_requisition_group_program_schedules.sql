CREATE TABLE requisition_group_program_schedules (
  requisitionGroupId INTEGER REFERENCES requisition_groups(id),
  programId INTEGER REFERENCES programs(id),
  scheduleId INTEGER REFERENCES schedules(id),
  modifiedBy VARCHAR(50),
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (requisitionGroupId, programId)
);