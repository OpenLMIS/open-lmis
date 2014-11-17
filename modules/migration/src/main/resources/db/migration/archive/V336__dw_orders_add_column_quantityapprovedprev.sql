DO $$ 
BEGIN 
        BEGIN            
	    ALTER TABLE dw_orders ADD COLUMN quantityapprovedprev integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column quantityapprovedprev already exists in dw_orders.';
        END;
END;
$$