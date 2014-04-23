DO $$ 
BEGIN 
  ALTER TABLE dw_orders ADD COLUMN emergency boolean;
  EXCEPTION
  WHEN duplicate_column THEN RAISE NOTICE 'column emergency already exists in dw_orders.';
 
END;
$$