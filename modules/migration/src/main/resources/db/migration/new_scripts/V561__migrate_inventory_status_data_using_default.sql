DO $$
BEGIN
  INSERT into equipment_inventory_statuses (inventoryid, statusid) (
    SELECT id, (SELECT id FROM equipment_operational_status WHERE name = 'Fully Operational')
    FROM equipment_inventories
  );
END;
$$