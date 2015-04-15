
DO $$ 
BEGIN 
   BEGIN
        BEGIN
        	ALTER TABLE alert_facility_stockedout  ADD COLUMN modifieddate date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column modifieddate already exists in alert_facility_stockedout.';
        END;
    END;

 
    BEGIN
        BEGIN
            ALTER TABLE alert_requisition_approved  ADD COLUMN modifieddate date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column modifieddate already exists in alert_requisition_approved.';
        END;
    END;
    
    BEGIN
        BEGIN
            ALTER TABLE alert_requisition_emergency  ADD COLUMN modifieddate date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column modifieddate already exists in alert_requisition_emergency.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE alert_requisition_pending  ADD COLUMN modifieddate date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column modifieddate already exists in alert_requisition_pending.';
        END;
    END;

    BEGIN
        BEGIN
            ALTER TABLE alert_requisition_rejected  ADD COLUMN modifieddate date;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column modifieddate already exists in alert_requisition_rejected.';
        END;
    END;


END;
$$