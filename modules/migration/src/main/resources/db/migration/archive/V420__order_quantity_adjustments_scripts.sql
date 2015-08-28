-- drop contraints
ALTER TABLE IF EXISTS order_quantity_adjustment_products DROP CONSTRAINT IF EXISTS order_quantity_adjustment_products_factorsId_fkey;
ALTER TABLE IF EXISTS order_quantity_adjustment_products DROP CONSTRAINT IF EXISTS order_quantity_adjustment_products_facilityId_fkey;
ALTER TABLE IF EXISTS order_quantity_adjustment_products DROP CONSTRAINT IF EXISTS order_quantity_adjustment_products_productId_fkey;
ALTER TABLE IF EXISTS order_quantity_adjustment_products DROP CONSTRAINT IF EXISTS order_quantity_adjustment_products_typeId_fkey;
ALTER TABLE IF EXISTS order_quantity_adjustment_products DROP CONSTRAINT IF EXISTS chk_order_quantity_adjustment_products_min_mos;

-- drop unique indexes
DROP INDEX IF EXISTS uc_order_quantity_adjustment_factors_name;
DROP INDEX IF EXISTS uc_order_quantity_adjustment_products_product;
DROP INDEX IF EXISTS uc_order_quantity_adjustment_types_name;

-- drop pk index
ALTER TABLE IF EXISTS order_quantity_adjustment_factors DROP CONSTRAINT  IF EXISTS order_quantity_adjustment_factors_pkey;
ALTER TABLE IF EXISTS order_quantity_adjustment_types DROP CONSTRAINT  IF EXISTS order_quantity_adjustment_types_pkey;
ALTER TABLE IF EXISTS  order_quantity_adjustment_products DROP CONSTRAINT  IF EXISTS order_quantity_adjustment_products_pkey;

-- drop tables
DROP TABLE  IF EXISTS  order_quantity_adjustment_products;
DROP TABLE  IF EXISTS  order_quantity_adjustment_types;
DROP TABLE  IF EXISTS  order_quantity_adjustment_factors;

-- create table
CREATE TABLE order_quantity_adjustment_types (
id serial NOT NULL,	
name varchar(50) NOT NULL,
description varchar(100),
displayOrder int4,
createdBy int4,
createdDate timestamp(6) DEFAULT now(),
modifiedBy int4,
modifiedDate timestamp(6) DEFAULT now(),
CONSTRAINT order_quantity_adjustment_types_pkey PRIMARY KEY (id) 
);

-- create unique index
CREATE UNIQUE INDEX uc_order_quantity_adjustment_types_name ON order_quantity_adjustment_types (name ASC);


COMMENT ON TABLE order_quantity_adjustment_types IS 'Adjustment types include:
* Rationing
* Seasonality
* Outbreak
* Malaria season
* Remote Facility
* MSL Physical Inventory close-out
* Other


This will be used in sending out the notifications to the facility and also display on the report';
COMMENT ON COLUMN order_quantity_adjustment_types.id IS 'ID';
COMMENT ON COLUMN order_quantity_adjustment_types.name IS 'Name';
COMMENT ON COLUMN order_quantity_adjustment_types.description IS 'Description';
COMMENT ON COLUMN order_quantity_adjustment_types.displayOrder IS 'Display Order';
COMMENT ON COLUMN order_quantity_adjustment_types.createdBy IS 'Created By';
COMMENT ON COLUMN order_quantity_adjustment_types.createdDate IS 'Created Date';
COMMENT ON COLUMN order_quantity_adjustment_types.modifiedBy IS 'Modified By';
COMMENT ON COLUMN order_quantity_adjustment_types.modifiedDate IS 'Modified Date';

-- create table
CREATE TABLE order_quantity_adjustment_factors (
id serial NOT NULL,
name varchar(50) NOT NULL,
description varchar(100),
displayOrder int4,
basedOnFormula bool DEFAULT false,
createdBy int4,
createdDate timestamp(6) DEFAULT now(),
modifiedBy int4,
modifiedDate timestamp(6) DEFAULT now(),
CONSTRAINT order_quantity_adjustment_factors_pkey PRIMARY KEY (id) 
);

-- add unique index
CREATE UNIQUE INDEX uc_order_quantity_adjustment_factors_name ON order_quantity_adjustment_factors (name ASC);
COMMENT ON TABLE order_quantity_adjustment_factors IS 'Basis of adjustment will enable the user to specify exact approach they need to use in adjusting for seasonality:
* Based on eZICS formula
* Based on Noel Watson
* Based on MOS AdjustmentThis will be used in sending out the notifications to the facility and also display on the report';
COMMENT ON COLUMN order_quantity_adjustment_factors.id IS 'ID';
COMMENT ON COLUMN order_quantity_adjustment_factors.name IS 'Name';
COMMENT ON COLUMN order_quantity_adjustment_factors.description IS 'Description';
COMMENT ON COLUMN order_quantity_adjustment_factors.displayOrder IS 'Display Order';
COMMENT ON COLUMN order_quantity_adjustment_factors.basedOnFormula IS 'Based On Formula';
COMMENT ON COLUMN order_quantity_adjustment_factors.createdBy IS 'Created By';
COMMENT ON COLUMN order_quantity_adjustment_factors.createdDate IS 'Created Date';
COMMENT ON COLUMN order_quantity_adjustment_factors.modifiedBy IS 'Modified By';
COMMENT ON COLUMN order_quantity_adjustment_factors.modifiedDate IS 'Modified Date';

-- create table
CREATE TABLE order_quantity_adjustment_products (
id serial NOT NULL,
facilityId int4 NOT NULL,
productId int4 NOT NULL,
typeId int4 NOT NULL,
factorId int4 NOT NULL,
startDate date,
endDate date,
minMonthsOfStock int4,
maxMonthsOfStock int4,
formula varchar(500),
createdBy int4,
createdDate timestamp(6) DEFAULT now(),
modifiedBy int4,
modifiedDate timestamp(6) DEFAULT now(),
CONSTRAINT order_quantity_adjustment_products_pkey PRIMARY KEY (id) 
);

-- add unique index
CREATE UNIQUE INDEX uc_order_quantity_adjustment_products_product ON order_quantity_adjustment_products (facilityId ASC, productId ASC, typeId ASC, factorId ASC);
COMMENT ON INDEX uc_order_quantity_adjustment_products_product IS 'One adjustment rule per facility per product';
COMMENT ON TABLE order_quantity_adjustment_products IS 'Adjust MOS for seasonality / rationing';
COMMENT ON COLUMN order_quantity_adjustment_products.id IS 'ID';
COMMENT ON COLUMN order_quantity_adjustment_products.facilityId IS 'Facility ID';
COMMENT ON COLUMN order_quantity_adjustment_products.productId IS 'Product ID';
COMMENT ON COLUMN order_quantity_adjustment_products.typeId IS 'Type ID';
COMMENT ON COLUMN order_quantity_adjustment_products.factorId IS 'Factor ID';
COMMENT ON COLUMN order_quantity_adjustment_products.startDate IS 'Start Date';
COMMENT ON COLUMN order_quantity_adjustment_products.endDate IS 'End Date';
COMMENT ON COLUMN order_quantity_adjustment_products.minmonthsofstock IS 'Minimum Months of Stock';
COMMENT ON COLUMN order_quantity_adjustment_products.maxmonthsofstock IS 'Maximum Months of Stock';
COMMENT ON COLUMN order_quantity_adjustment_products.formula IS 'Formula';
COMMENT ON COLUMN order_quantity_adjustment_products.createdBy IS 'Created By';
COMMENT ON COLUMN order_quantity_adjustment_products.createdDate IS 'Created Date';
COMMENT ON COLUMN order_quantity_adjustment_products.modifiedBy IS 'Modified By';
COMMENT ON COLUMN order_quantity_adjustment_products.modifiedDate IS 'Modified Date';

-- add fkeys
ALTER TABLE order_quantity_adjustment_products ADD CONSTRAINT order_quantity_adjustment_products_factorsId_fkey FOREIGN KEY (factorId) REFERENCES order_quantity_adjustment_factors (id);
ALTER TABLE order_quantity_adjustment_products ADD CONSTRAINT order_quantity_adjustment_products_typeId_fkey FOREIGN KEY (typeId) REFERENCES order_quantity_adjustment_types (id);
ALTER TABLE order_quantity_adjustment_products ADD CONSTRAINT order_quantity_adjustment_products_facilityId_fkey FOREIGN KEY (facilityId) REFERENCES facilities (id);
ALTER TABLE order_quantity_adjustment_products ADD CONSTRAINT order_quantity_adjustment_products_productId_fkey FOREIGN KEY (productId) REFERENCES products (id);


ALTER TABLE IF EXISTS order_quantity_adjustment_products add constraint chk_order_quantity_adjustment_products_min_mos check (COALESCE(minMonthsOfStock,0) < COALESCE(maxMonthsOfStock,1));

--- see order_quantity_adjustment_types
INSERT INTO order_quantity_adjustment_types(name, description, displayOrder)   VALUES ('Rationing','Rationing',1);
INSERT INTO order_quantity_adjustment_types(name, description, displayOrder)   VALUES ('Seasonality','Seasonality',2);
INSERT INTO order_quantity_adjustment_types(name, description, displayOrder)   VALUES ('Outbreak','Outbreak',3);
INSERT INTO order_quantity_adjustment_types(name, description, displayOrder)   VALUES ('Malaria season','Malaria season',4);
INSERT INTO order_quantity_adjustment_types(name, description, displayOrder)   VALUES ('Remote Facility','Remote Facility',5);
INSERT INTO order_quantity_adjustment_types(name, description, displayOrder)   VALUES ('MSL Physical Inventory close-out','MSL Physical Inventory close-out',6);
INSERT INTO order_quantity_adjustment_types(name, description, displayOrder)   VALUES ('Other','Other',7);

-- see order_quantity_adjustment_factors
INSERT INTO order_quantity_adjustment_factors(name, description, displayOrder)   VALUES ('Based on eZICS formula','Based on eZICS formula',1);
INSERT INTO order_quantity_adjustment_factors(name, description, displayOrder)   VALUES ('Based on Noel Watson','Based on Noel Watson',2);
INSERT INTO order_quantity_adjustment_factors(name, description, displayOrder)   VALUES ('Based on MOS Adjustment','Based on MOS Adjustment',3);

-- INSERT INTO order_quantity_adjustment_products (facilityid, productid, typeid, factorid, minMonthsofStock, maxMonthsOfStock)
-- VALUES ((select max(id) from facilities),(select max(id) from products),(select max(id) from order_quantity_adjustment_types),(select max(id) from order_quantity_adjustment_factors),1,2);
--select * from order_quantity_adjustment_types;
--select * from order_quantity_adjustment_factors;
--select * from order_quantity_adjustment_products;