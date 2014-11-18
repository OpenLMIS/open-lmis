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


    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN requisitiongroupid integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column requisitiongroupid already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN requisitiongroupname character varying(50);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column requisitiongroupname already exists in dw_orders.';
        END;
    END;


    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN facilitytypeid integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column facilitytypeid already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN facilitytypename character varying(50);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column facilitytypename already exists in dw_orders.';
        END;
    END;



    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN scheduleid integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column scheduleid already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN schedulename character varying(50);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column schedulename already exists in dw_orders.';
        END;
    END;


    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN productcategoryid integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column productcategoryid already exists in dw_orders.';
        END;
    END;
    

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN productcategoryname character varying(150);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column productcategoryname already exists in dw_orders.';
        END;
    END;


    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN productgroupid integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column productgroupid already exists in dw_orders.';
        END;
    END;


    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN productgroupname character varying(250);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column productgroupname already exists in dw_orders.';
        END;
    END;

END;
$$