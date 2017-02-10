DROP INDEX IF EXISTS idx_vw_period_movements;
CREATE UNIQUE INDEX idx_vw_period_movements
  ON vw_period_movements (uuid, periodStart, periodEnd, facility_code);