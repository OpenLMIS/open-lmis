CREATE TABLE signatures (
  id           SERIAL PRIMARY KEY,
  type         VARCHAR NOT NULL,
  text         VARCHAR NOT NULL,
  createdBy    INTEGER,
  createdDate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy   INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);