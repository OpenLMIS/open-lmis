DO $$
  BEGIN
    BEGIN
      ALTER TABLE losses_adjustments_types ADD COLUMN isdefault boolean DEFAULT true;
    EXCEPTION
      WHEN duplicate_column THEN RAISE NOTICE 'column isdefault already exists in losses_adjustments_types.';
    END;
  END;
$$