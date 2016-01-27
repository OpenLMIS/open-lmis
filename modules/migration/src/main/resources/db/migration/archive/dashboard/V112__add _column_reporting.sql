DO $$ 
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN reporting character(1);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column reporting already exists in dw_orders.';
        END;
    END;
$$