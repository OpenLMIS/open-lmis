CREATE TABLE requisition_period(
  id SERIAL PRIMARY KEY,
  rnrId INT NOT NULL REFERENCES requisitions(id) UNIQUE,
  periodStartDate TIMESTAMP,
  periodEndDate TIMESTAMP,
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);