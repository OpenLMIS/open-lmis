DROP TABLE IF EXISTS shipment_configuration;

CREATE TABLE shipment_configuration (
  headerInFile            BOOLEAN     NOT NULL,
  packedDatePattern       VARCHAR(25) NOT NULL,
  shippedDatePattern      VARCHAR(25) NOT NULL,
  createdBy               INTEGER,
  createdDate             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy              INTEGER,
  modifiedDate            TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
