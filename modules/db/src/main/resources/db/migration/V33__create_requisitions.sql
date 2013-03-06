DROP TABLE IF EXISTS requisitions;
CREATE TABLE requisitions (
  id                              SERIAL PRIMARY KEY,
  facilityId                      INTEGER     NOT NULL REFERENCES facilities (id),
  programId                       INTEGER     NOT NULL REFERENCES programs (id),
  periodId                        INTEGER     NOT NULL REFERENCES processing_periods (id),
  status                          VARCHAR(20) NOT NULL,
  fullSupplyItemsSubmittedCost    NUMERIC(15, 4),
  nonFullSupplyItemsSubmittedCost NUMERIC(15, 4),
  supervisoryNodeId               INTEGER     REFERENCES supervisory_nodes (id),
  supplyingFacilityId             INTEGER REFERENCES facilities(id),
  orderBatchId                    INTEGER REFERENCES order_batches(id),
  modifiedBy                      INTEGER,
  submittedDate                   TIMESTAMP,
  modifiedDate                    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  creationDate                    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (facilityId, programId, periodId)
);