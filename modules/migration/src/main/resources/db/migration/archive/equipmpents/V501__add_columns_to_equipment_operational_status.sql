DO $$
  BEGIN
    BEGIN
      ALTER TABLE equipment_operational_status ADD COLUMN category text NULL;
    EXCEPTION
      WHEN duplicate_column THEN RAISE NOTICE 'column category already exists in equipment_operational_status.';
    END;

    BEGIN
      ALTER TABLE equipment_operational_status ADD COLUMN isbad boolean NULL;
    EXCEPTION
      WHEN duplicate_column THEN RAISE NOTICE 'column isbad already exists in equipment_operational_status.';
    END;

    BEGIN
      ALTER TABLE equipment_inventories
        ADD COLUMN notfunctionalstatusid integer;
      ALTER TABLE equipment_inventories
        ADD CONSTRAINT equipment_inventories_notfunctionalstatusid_fkey FOREIGN KEY (notfunctionalstatusid) REFERENCES equipment_operational_status (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
    END;
  END;
$$

