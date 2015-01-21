DO $$ 
BEGIN 
        BEGIN
          INSERT INTO product_categories(
            code, name, displayorder, createdby, createddate, modifiedby, modifieddate)
            VALUES ('vit', 'Vitamins', 102, 1, now(), 1, now());
        EXCEPTION
            WHEN others THEN RAISE NOTICE 'product category already exists';
        END;
END;
$$