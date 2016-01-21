DROP  TABLE IF EXISTS vaccine_doses cascade;
CREATE TABLE vaccine_doses
(
  id                SERIAL PRIMARY KEY,
  name              VARCHAR (100) UNIQUE NOT NULL,
  description       VARCHAR (200) NULL,
  displayOrder      INTEGER NOT NULL,
  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);

-- seed the vaccine dose table

INSERT INTO vaccine_doses (name, displayOrder)
  VALUES ('Dose 1',1), ('Dose 2',2), ('Dose 3',3), ('Dose 4',4), ('Dose 5',5)
;
DROP  TABLE IF EXISTS vaccine_product_doses cascade;
CREATE TABLE vaccine_product_doses
(
  id                SERIAL PRIMARY KEY,
  doseId            INTEGER NOT NULL REFERENCES vaccine_doses(id),
  programId         INTEGER NOT NULL REFERENCES programs(id),
  productId         INTEGER NOT NULL REFERENCES products (id),
  isActive          BOOLEAN,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);
DROP  TABLE IF EXISTS vaccine_diseases cascade;
CREATE TABLE vaccine_diseases
(
  id                SERIAL PRIMARY KEY,
  name              VARCHAR(100) UNIQUE NOT NULL,
  description       VARCHAR(200) NULL,
  displayOrder      INTEGER NOT NULL,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);

-- seed vaccine diseases
INSERT INTO vaccine_diseases (name, description, displayOrder)
VALUES  ('Fever and rash', 'Fever and rash',1),
        ('Neonatal tetanus','Neonatal tetanus',2),
        ('AFP', 'AFP cases',3);

DROP  TABLE IF EXISTS vaccine_reports cascade;
CREATE TABLE vaccine_reports
(
  id                SERIAL PRIMARY KEY,
  periodId          INTEGER NOT NULL REFERENCES processing_periods(id),
  programId         INTEGER NOT NULL REFERENCES programs(id),
  facilityId        INTEGER NOT NULL REFERENCES facilities(id),
  status            VARCHAR (100) NOT NULL,
  supervisoryNodeId INTEGER NULL,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);
DROP TABLE IF EXISTS vaccine_report_logistics_line_items cascade;
CREATE TABLE vaccine_report_logistics_line_items
(
  id                SERIAL PRIMARY KEY,
  reportId          INTEGER NOT NULL REFERENCES vaccine_reports(id),
  productId         INTEGER NOT NULL REFERENCES products(id),
  productCode       VARCHAR (100) NOT NULL,
  productName       VARCHAR (200) NOT NULL,

  displayOrder      INTEGER NOT NULL,

  openingBalance      INTEGER NULL,
  quantityReceived    INTEGER NULL,
  quantityIssued      INTEGER NULL,
  quantityVvmAlerted  INTEGER NULL,
  quantityFreezed     INTEGER NULL,
  quantityExpired     INTEGER NULL,
  quantityDiscardedUnopened INTEGER NULL,
  quantityDiscardedOpened   INTEGER NULL,
  quantityWastedOther       INTEGER NULL,

  endingBalance     INTEGER NULL,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);
DROP  TABLE IF EXISTS vaccine_report_adverse_effect_line_items cascade;
CREATE TABLE vaccine_report_adverse_effect_line_items
(
  id                SERIAL PRIMARY KEY,
  reportId          INTEGER NOT NULL REFERENCES vaccine_reports(id),
  productId         INTEGER NOT NULL REFERENCES products(id),

  date              DATE NULL,

  manufacturerId    INTEGER NULL,
  batch             VARCHAR (100) NOT NULL,
  expiry            DATE NULL,

  cases             INTEGER NOT NULL,
  investigation     VARCHAR (2000) NULL,
  notes             VARCHAR (2000) NULL,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);
DROP  TABLE IF EXISTS vaccine_report_coverage_line_items cascade;
CREATE TABLE vaccine_report_coverage_line_items(
  id                SERIAL PRIMARY KEY,
  reportId          INTEGER NOT NULL REFERENCES vaccine_reports(id),

  productId         INTEGER NOT NULL REFERENCES products(id),
  doseId            INTEGER NOT NULL REFERENCES vaccine_doses(id),
  isActive          BOOLEAN NOT NULL DEFAULT (FALSE),
  regular           INTEGER NULL,
  outreach          INTEGER NULL,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP

);
DROP  TABLE IF EXISTS vaccine_report_disease_line_items cascade;
CREATE TABLE vaccine_report_disease_line_items
(
  id                SERIAL PRIMARY KEY,
  reportId          INTEGER NOT NULL REFERENCES vaccine_reports(id),
  diseaseId         INTEGER NOT NULL REFERENCES vaccine_diseases(id),
  diseaseName       VARCHAR (200) NOT NULL,
  displayOrder      INTEGER NOT NULL,
  cases             INTEGER NULL,
  death             INTEGER NULL,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);


