DROP TABLE IF EXISTS requisition;
CREATE TABLE requisition (
  id SERIAL PRIMARY KEY,
  facilityId INTEGER NOT NULL REFERENCES facilities(id),
  programId INTEGER NOT  NULL REFERENCES programs(id),
  status VARCHAR(20) NOT NULL,
  fullSupplyItemsSubmittedCost FLOAT,
  nonFullSupplyItemsSubmittedCost FLOAT,
  totalSubmittedCost FLOAT ,
  modifiedBy VARCHAR(50),
  modifiedDate TIMESTAMP  DEFAULT  CURRENT_TIMESTAMP,
  check (totalSubmittedCost = fullSupplyItemsSubmittedCost + nonFullSupplyItemsSubmittedCost),
  UNIQUE(facilityId,programId)
);