CREATE TABLE demographic_estimate_categories(
  id              SERIAL PRIMARY KEY,
  name            VARCHAR (100) UNIQUE NOT NULL,
  description     VARCHAR (1000) NULL,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);