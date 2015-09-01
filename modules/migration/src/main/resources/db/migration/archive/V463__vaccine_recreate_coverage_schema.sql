DROP  TABLE IF EXISTS vaccine_report_coverage_line_items cascade;
DROP  TABLE IF EXISTS vaccine_product_doses cascade;
DROP  TABLE IF EXISTS vaccine_doses cascade;



CREATE TABLE vaccine_doses
(
  id                SERIAL PRIMARY KEY,
  name              VARCHAR (100) UNIQUE NOT NULL,
  description       VARCHAR (1000) NULL,
  displayOrder      INTEGER NOT NULL,
  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);

-- seed the vaccine dose table,
-- these are required of the application lookup
INSERT INTO vaccine_doses (name, displayOrder)
  VALUES ('Dose 1',1), ('Dose 2',2), ('Dose 3',3), ('Dose 4',4), ('Dose 5',5) , ('Dose 6', 6);

CREATE TABLE vaccine_product_doses
(
  id                SERIAL PRIMARY KEY,
  doseId            INTEGER NOT NULL REFERENCES vaccine_doses(id),
  programId         INTEGER NOT NULL REFERENCES programs(id),
  productId         INTEGER NOT NULL REFERENCES products (id),

  displayName       VARCHAR(100) NOT NULL,
  displayOrder      INTEGER NOT NULL,

  trackMale         BOOLEAN,
  trackFemale       BOOLEAN,

  denominatorEstimateCategoryId INTEGER REFERENCES demographic_estimate_categories( id ) NULL,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE vaccine_report_coverage_line_items(
  id                SERIAL PRIMARY KEY,
  skipped           BOOLEAN NOT NULL DEFAULT (false),
  reportId          INTEGER NOT NULL REFERENCES vaccine_reports(id),
  productId         INTEGER NOT NULL REFERENCES products(id),
  doseId            INTEGER NOT NULL REFERENCES vaccine_doses(id),

  displayOrder      INTEGER NOT NULL,
  displayName       VARCHAR(100) NOT NULL,

  trackMale         BOOLEAN NOT NULL DEFAULT (TRUE),
  trackFemale       BOOLEAN NOT NULL DEFAULT (TRUE),

  regularMale       INTEGER NULL,
  regularFemale     INTEGER NULL,

  outreachMale      INTEGER NULL,
  outreachFemale    INTEGER NULL,

  campaignMale      INTEGER NULL,
  campaignFemale    INTEGER NULL,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);