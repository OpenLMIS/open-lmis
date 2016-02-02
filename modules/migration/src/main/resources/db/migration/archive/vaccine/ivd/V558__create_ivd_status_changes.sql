CREATE TABLE vaccine_report_status_changes
(
  id                        SERIAL PRIMARY KEY,
  reportId                  INTEGER NOT NULL REFERENCES vaccine_reports (id),
  status                    varchar(50) NOT NULL,


  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);