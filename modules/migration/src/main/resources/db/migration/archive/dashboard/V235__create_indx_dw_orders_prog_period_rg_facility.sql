DO $$ 
    BEGIN
        BEGIN
            CREATE INDEX indx_dw_orders_prog_period_rg_facility ON dw_orders (programid, periodid, requisitiongroupid, facilityid);
        EXCEPTION
            WHEN others THEN RAISE NOTICE 'can not create index';
        END;
    END;
$$