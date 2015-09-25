DO $$ 
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN stocking character(1);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column stocking already exists in dw_orders.';
        END;
    END;
$$