CREATE TABLE requisition_period(
  id SERIAL PRIMARY KEY,
  requisitionsId INT NOT NULL REFERENCES requisitions(id) UNIQUE,
  actualPeriodStartDate TIMESTAMP,
  actualPeriodEndDate TIMESTAMP,
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
);