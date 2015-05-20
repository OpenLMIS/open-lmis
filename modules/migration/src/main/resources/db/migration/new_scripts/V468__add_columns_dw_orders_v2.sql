
DO $$ 
BEGIN 
   BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN programcode character varying(80);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column programcode already exists in dw_orders.';
        END;
    END;

 
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN periodstartdate date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column periodstartdate already exists in dw_orders.';
        END;
    END;    
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN periodenddate date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column periodenddate already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN rnrmodifieddate date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column rnrmodifieddate already exists in dw_orders.';
        END;
    END;

END;
$$