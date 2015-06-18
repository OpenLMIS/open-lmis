DO $$
BEGIN
  ALTER TABLE equipment_inventory_statuses RENAME COLUMN effectivedatetime TO createddate;
  ALTER TABLE equipment_inventory_statuses ADD COLUMN createdby integer;
  ALTER TABLE equipment_inventory_statuses ADD COLUMN modifiedby integer;
  ALTER TABLE equipment_inventory_statuses ADD COLUMN modifieddate timestamp with time zone DEFAULT NOW();
END;
$$