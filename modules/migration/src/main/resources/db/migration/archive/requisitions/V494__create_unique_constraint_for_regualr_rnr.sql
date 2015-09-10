CREATE UNIQUE INDEX unique_index_regular_rnr
  ON requisitions (facilityId, programId, periodId)
    WHERE (emergency = false);