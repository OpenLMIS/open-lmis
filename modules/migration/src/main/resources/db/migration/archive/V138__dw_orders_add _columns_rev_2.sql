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
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN programid integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column programid already exists in dw_orders.';
        END;
    END;
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN reporting character(1);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column reporting already exists in dw_orders.';
        END;
    END;
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN stocking character(1);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column stocking already exists in dw_orders.';
        END;
    END;
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
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN stockedoutinpast boolean;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column stockedoutinpast already exists in dw_orders.';
        END;
    END;
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN suppliedinpast boolean;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column suppliedinpast already exists in dw_orders.';
        END;
    END;
    BEGIN
        BEGIN
            ALTER TABLE dw_orders ADD COLUMN mossuppliedinpast numeric(6,1);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column mossuppliedinpast already exists in dw_orders.';
        END;
    END;
END;
$$