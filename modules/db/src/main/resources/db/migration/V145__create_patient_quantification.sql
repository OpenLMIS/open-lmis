DROP TABLE IF EXISTS patient_quantification_line_items;
CREATE TABLE patient_quantification_line_items (
  id                          SERIAL PRIMARY KEY,
  category                    VARCHAR(50),
  total                       INTEGER,
  rnrId                       INTEGER NOT NULL REFERENCES requisitions (id),
  createdBy                   INTEGER,
  createdDate                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                  INTEGER,
  modifiedDate                TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);