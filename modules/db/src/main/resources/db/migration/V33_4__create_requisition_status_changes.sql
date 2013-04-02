DROP TABLE IF EXISTS requisition_status_changes;
CREATE TABLE requisition_status_changes (
  id                              SERIAL PRIMARY KEY,
  rnrId                           INTEGER     NOT NULL REFERENCES requisitions (id),
  status                          VARCHAR(20) NOT NULL,
  statusChangedBy                 INTEGER NOT NULL REFERENCES users(id),
  statusChangeDate                TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
