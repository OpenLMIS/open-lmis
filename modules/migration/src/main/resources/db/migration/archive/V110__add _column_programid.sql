DO $$ 
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN programid integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column programid already exists in dw_orders.';
        END;
    END;
$$