-- Table: mos_adjustment_products
DROP TABLE IF EXISTS mos_adjustment_products;
CREATE TABLE mos_adjustment_products
(
  id SERIAL PRIMARY KEY,  -- id                            ,
  typeId integer REFERENCES mos_adjustment_types (id) NOT NULL, -- name
  basisId integer REFERENCES mos_adjustment_basis (id) NOT NULL,
  productID integer REFERENCES products (id) NOT NULL,
  startDate date, -- startDate
  endDate date, -- endDate
  createdBy integer, -- createdBy
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- createdDate
  modifiedBy integer, -- modifiedBy
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- modifiedDate
);
CREATE UNIQUE INDEX uc_mos_adjustment_products_product ON mos_adjustment_products(typeId,basisId,productId);
COMMENT ON TABLE mos_adjustment_products IS  'Adjust MOS for seasonality / rationing'; 
COMMENT ON COLUMN mos_adjustment_products.id IS 'id';
COMMENT ON COLUMN mos_adjustment_products.typeId IS 'typeId';
COMMENT ON COLUMN mos_adjustment_products.basisId IS 'basisId';
COMMENT ON COLUMN mos_adjustment_products.startDate IS 'startDate';
COMMENT ON COLUMN mos_adjustment_products.endDate IS 'endDate';
COMMENT ON COLUMN mos_adjustment_products.productID IS 'productID';
COMMENT ON COLUMN mos_adjustment_products.createdBy IS 'createdBy';
COMMENT ON COLUMN mos_adjustment_products.createdDate IS 'createdDate';
COMMENT ON COLUMN mos_adjustment_products.modifiedBy IS 'modifiedBy';
COMMENT ON COLUMN mos_adjustment_products.modifiedDate IS 'modifiedDate';


