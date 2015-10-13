DO $$ 
BEGIN 
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN modifieddate timestamp without time zone;
	    ALTER TABLE dw_orders ALTER COLUMN modifieddate SET DEFAULT now();

        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column modifieddate already exists in dw_orders.';
        END;
    END;
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN tracer boolean;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column tracer already exists in dw_orders.';
        END;
    END;
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN skipped boolean;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column skipped already exists in dw_orders.';
        END;
    END;
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN stockoutdays integer;
            ALTER TABLE dw_orders ALTER COLUMN stockoutdays SET DEFAULT 0;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column stockoutdays already exists in dw_orders.';
        END;
    END; 
END;
$$