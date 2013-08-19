DROP TABLE IF EXISTS configurations;
CREATE TYPE date_patterns AS ENUM ('ddMMyy', 'ddMMyyyy', 'MMddyy', 'MMddyyyy', 'yyyyMMdd');
CREATE TYPE period_patterns AS ENUM ('MMyy', 'MMyyyy', 'yyMM', 'yyyyMM');

CREATE TABLE configurations (
  orderFilePrefix    VARCHAR(8)      NOT NULL,
  headersInOrderFile BOOLEAN         NOT NULL,
  orderDatePattern   date_patterns   NOT NULL,
  periodDatePattern  period_patterns NOT NULL,
  createdBy          INTEGER,
  createdDate        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy         INTEGER,
  modifiedDate       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

