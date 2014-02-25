DO $$ 
BEGIN 
   BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN programname character varying(50);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column programname already exists in dw_orders.';
        END;
    END;

   BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN facilityname character varying(50);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column facilityname already exists in dw_orders.';
        END;
    END;

 
 BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN productprimaryname character varying(150);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column productprimaryname already exists in dw_orders.';
        END;
    END;
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN productfullname character varying(250);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column productfullname already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN geographiczonename character varying(250);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column geographiczonename already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN processingperiodname character varying(250);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column processingperiodname already exists in dw_orders.';
        END;
    END;
END;
$$