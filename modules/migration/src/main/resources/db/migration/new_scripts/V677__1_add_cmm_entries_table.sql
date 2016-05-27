DROP TABLE IF EXISTS cmm_entries;

CREATE TABLE  cmm_entries
(
  id SERIAL PRIMARY KEY,
  productCode VARCHAR(50) NOT NULL REFERENCES products(code),
  facilityId INTEGER NOT NULL REFERENCES facilities(id),
  periodBegin DATE NOT NULL,
  periodEnd DATE NOT NULL,
  cmmValue FLOAT,
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);