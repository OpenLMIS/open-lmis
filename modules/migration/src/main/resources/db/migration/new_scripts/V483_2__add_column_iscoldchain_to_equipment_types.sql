DO $$
  BEGIN
    ALTER TABLE equipment_types ADD COLUMN iscoldchain boolean NULL;
  EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column iscoldchain already exists in equipment_types.';
  END;
$$

