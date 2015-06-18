  DO $$
  BEGIN
    ALTER TABLE programs ADD usepriceschedule BOOLEAN NOT NULL DEFAULT(false);
  EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column usepriceschedule already exists in programs table.';
  END;
$$
