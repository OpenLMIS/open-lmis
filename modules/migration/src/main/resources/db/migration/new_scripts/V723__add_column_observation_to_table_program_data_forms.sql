DO $$ 
    BEGIN
        BEGIN
            ALTER TABLE program_data_forms ADD COLUMN observation varchar;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column observation already exists in program_data_forms.';
        END;
    END;
$$
