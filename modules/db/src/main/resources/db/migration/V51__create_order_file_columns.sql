DROP TABLE IF EXISTS order_file_columns;

CREATE TABLE order_file_columns (
  id                 SERIAL PRIMARY KEY,
  dataFieldLabel     VARCHAR(50),
  fieldName          VARCHAR(50),
  nestedPath         VARCHAR(50),
  includeInOrderFile BOOLEAN NOT NULL DEFAULT TRUE,
  columnLabel        VARCHAR(50),
  position           INTEGER NOT NULL,
  openLmisField      BOOLEAN NOT NULL DEFAULT FALSE,
  createdBy          INTEGER,
  createdDate        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy         INTEGER,
  modifiedDate       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
