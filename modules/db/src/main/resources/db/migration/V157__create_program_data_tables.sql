CREATE TABLE supplemental_programs (
  id                        SERIAL PRIMARY KEY,
  code                      VARCHAR(50) NOT NULL UNIQUE,
  name                      VARCHAR(50),
  description               VARCHAR(100),
  active                    BOOLEAN,
  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uc_supplemental_programs_lower_code ON programs (LOWER(code));

CREATE TABLE program_data_forms (
  id                      SERIAL PRIMARY KEY,
  supplementalProgramId   INTEGER REFERENCES supplemental_programs(id),
  periodId                INTEGER REFERENCES processing_periods(id),
  createdBy               INTEGER,
  modifiedBy              INTEGER,
  createdDate             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedDate            TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE program_data_columns (
  id                    SERIAL PRIMARY KEY,
  code                  VARCHAR(50) NOT NULL,
  label                 VARCHAR(250),
  description           TEXT,
  supplmentalProgramId  INTEGER REFERENCES supplemental_programs(id),
  createdBy             INTEGER,
  modifiedBy            INTEGER,
  createdDate           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedDate          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE program_data_items (
  id                SERIAL PRIMARY KEY,
  formId            INTEGER REFERENCES program_data_forms(id),
  name              VARCHAR(50),
  templateColumnId  INTEGER REFERENCES program_data_columns(id),
  value             INTEGER,
  createdBy         INTEGER,
  modifiedBy        INTEGER,
  createdDate       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedDate      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);