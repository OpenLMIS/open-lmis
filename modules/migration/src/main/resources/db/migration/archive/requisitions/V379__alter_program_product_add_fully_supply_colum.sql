DO $$
    BEGIN
        BEGIN
            ALTER TABLE program_products ADD COLUMN fullSupply boolean;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column fullSupply already exists in program_products.';
        END;
    END;
$$