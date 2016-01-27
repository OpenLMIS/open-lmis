DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM rights WHERE name = 'VIEW_CCE_STORAGE_CAPACITY_REPORT') THEN
    INSERT INTO rights (name, rightType, description, displaynamekey)
    VALUES ('VIEW_CCE_STORAGE_CAPACITY_REPORT','REPORT','Permission to view CCE Storage Capacity Report','right.report.cce.storage.capacity');
  END IF;
END;
$$