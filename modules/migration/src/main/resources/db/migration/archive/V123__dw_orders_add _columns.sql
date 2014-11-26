DO $$ 
BEGIN 
   BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN soh integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column soh already exists in dw_orders.';
        END;
    END;

 
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN amc integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column amc already exists in dw_orders.';
        END;
    END;
    
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN mos numeric(6,1);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column mos already exists in dw_orders.';
        END;
    END;

END;
$$