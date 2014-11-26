DO $$ 
    BEGIN
        BEGIN
            CREATE INDEX indx_dw_orders_status ON dw_orders (status);
        EXCEPTION
            WHEN others THEN RAISE NOTICE 'can not create index';
        END;
    END;
$$