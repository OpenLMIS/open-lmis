DROP TABLE IF EXISTS requisition;
CREATE TABLE requisition (
  id                              SERIAL PRIMARY KEY,
  facilityId                      INTEGER     NOT NULL REFERENCES facilities (id),
  programId                       INTEGER     NOT NULL REFERENCES programs (id),
  periodId                        INTEGER     NOT NULL REFERENCES processing_periods (id),
  status                          VARCHAR(20) NOT NULL,
  fullSupplyItemsSubmittedCost    NUMERIC(15, 4),
  nonFullSupplyItemsSubmittedCost NUMERIC(15, 4),
  modifiedBy                      VARCHAR(50),
  submittedDate                   TIMESTAMP,
  modifiedDate                    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (facilityId, programId, periodId)
);