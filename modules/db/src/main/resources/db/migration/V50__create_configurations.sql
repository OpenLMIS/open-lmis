DROP TABLE IF EXISTS configurations;

CREATE TABLE configurations (
  orderFilePrefix   VARCHAR(8),
  headerInOrderFile BOOLEAN     NOT NULL,
  orderDatePattern  VARCHAR(25) NOT NULL,
  periodDatePattern VARCHAR(25) NOT NULL,
  createdBy         INTEGER,
  createdDate       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

