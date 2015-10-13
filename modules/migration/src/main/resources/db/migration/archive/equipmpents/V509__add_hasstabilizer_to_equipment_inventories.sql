DO $$
  BEGIN
    ALTER TABLE equipment_inventories ADD COLUMN hasstabilizer boolean;
  EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column hasstabilizer already exists in equipment_inventories.';
  END;
$$

