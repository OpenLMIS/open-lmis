-- create the new column for the category id
ALTER TABLE program_products
ADD COLUMN productcategoryid INT NULL REFERENCES product_categories (id);


-- migrate the existing product categories
UPDATE program_products SET productcategoryid = p.categoryid
FROM products p where p.id = program_products.productid;

-- add the configuration option to force the category to either be the one on the program products or programs table.
INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('ALLOW_PRODUCT_CATEGORY_PER_PROGRAM', 'Allow product categories at program level','Settings','','false', 'BOOLEAN', 12);

