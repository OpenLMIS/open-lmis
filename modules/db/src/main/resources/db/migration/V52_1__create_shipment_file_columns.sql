DROP TABLE IF EXISTS shipment_file_columns;

CREATE TABLE shipment_file_columns (
  id                        SERIAL PRIMARY KEY,
  dataFieldLabel            VARCHAR(50),
  position                  INTEGER NOT NULL,
  includedInShipmentFile    BOOLEAN NOT NULL,
  mandatory                 BOOLEAN NOT NULL,
  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);