DO $$ 
BEGIN 
   BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN initiateddate timestamp;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column initiateddate already exists in dw_orders.';
        END;
    END;

 
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN submitteddate timestamp;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column submitteddate already exists in dw_orders.';
        END;
    END;
    
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN authorizeddate timestamp;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column authorizeddate already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN inapprovaldate timestamp;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column inapprovaldate already exists in dw_orders.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN releaseddate timestamp;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column approveddate already exists in dw_orders.';
        END;
    END;
END;
$$