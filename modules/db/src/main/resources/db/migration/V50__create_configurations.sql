DROP TABLE IF EXISTS order_configurations;

CREATE TABLE order_configurations (
  filePrefix        VARCHAR(8),
  headerInFile      BOOLEAN     NOT NULL,
  datePattern       VARCHAR(25) NOT NULL,
  periodDatePattern VARCHAR(25) NOT NULL,
  createdBy         INTEGER,
  createdDate       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

