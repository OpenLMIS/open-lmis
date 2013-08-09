DROP TABLE IF EXISTS refrigerators;
CREATE TABLE refrigerators (
  id  SERIAL PRIMARY KEY,
  brand VARCHAR(20) NOT NULL,
  model VARCHAR(20) NOT NULL,
  serialNumber VARCHAR(30) UNIQUE NOT NULL,
  facilityId   INTEGER references facilities(id),
  createdBy      INTEGER                                 NOT NULL REFERENCES users (id),
  createdDate    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy     INTEGER                                 NOT NULL REFERENCES users (id),
  modifiedDate   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
