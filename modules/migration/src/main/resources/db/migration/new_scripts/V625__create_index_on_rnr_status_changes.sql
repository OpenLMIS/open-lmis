CREATE INDEX requisition_status_changes_rnrid_and_status
  ON requisition_status_changes (rnrid, status);