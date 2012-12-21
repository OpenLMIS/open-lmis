DROP TABLE IF EXISTS requisition;
CREATE TABLE requisition (
  id SERIAL PRIMARY KEY,
  facility_id INTEGER NOT NULL REFERENCES facilities(id),
  program_code VARCHAR(50) NOT  NULL REFERENCES programs(code),
  status VARCHAR(20) NOT NULL,
  fullSupplyItemsSubmittedCost FLOAT,
  nonFullSupplyItemsSubmittedCost FLOAT,
  totalSubmittedCost FLOAT ,
  modified_by VARCHAR(50),
  modified_date TIMESTAMP  DEFAULT  CURRENT_TIMESTAMP,
  check (totalSubmittedCost = fullSupplyItemsSubmittedCost + nonFullSupplyItemsSubmittedCost)
);