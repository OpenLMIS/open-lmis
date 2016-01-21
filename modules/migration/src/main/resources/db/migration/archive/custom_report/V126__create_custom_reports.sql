create table custom_reports
(
  id                        SERIAL PRIMARY KEY,
  reportKey                 VARCHAR(50) NOT NULL UNIQUE,
  name                      VARCHAR(50),
  description               VARCHAR(50),
  active                    BOOLEAN,
  createdBy                 INTEGER,
  help                      VARCHAR(5000),
  filters                   VARCHAR(5000),
  query                     VARCHAR(5000),
  category                  VARCHAR(5000),
  columnOptions             VARCHAR(5000),
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);