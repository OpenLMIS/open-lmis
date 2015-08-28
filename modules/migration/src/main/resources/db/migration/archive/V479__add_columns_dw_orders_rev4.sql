
DO $$ 
BEGIN 

   BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN programCode character varying(80);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column programCode already exists in dw_orders.';
        END;
    END;
 
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN periodStartDate date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column periodStartDate already exists in dw_orders.';
        END;
    END;
    
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN periodEndDate date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column periodEndDate already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN openingBalance integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column openingBalance already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN adjustment integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column adjustment already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN quantityOrdered integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column quantityOrdered already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN quantityShipped integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column quantityShipped already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN dateOrdered date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column dateOrdered already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN dateShipped date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column dateShipped already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN dispensed integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column dispensed already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN rnrModifieDdate date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column rnrModifieDdate already exists in dw_orders.';
        END;
    END;


   BEGIN
        BEGIN
		ALTER TABLE dw_orders
		  ADD COLUMN rmnch boolean DEFAULT 'N';
		COMMENT ON COLUMN dw_orders.rmnch IS 'true if product is part of RMNCH program';

        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column rmnch already exists in dw_orders.';
        END;
    END;

END;
$$