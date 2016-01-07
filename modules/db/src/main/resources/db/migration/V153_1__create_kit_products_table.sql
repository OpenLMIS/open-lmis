CREATE TABLE kit_products_relation (
  kitCode VARCHAR(50) NOT NULL REFERENCES products(code),
  productCode VARCHAR(50) NOT NULL REFERENCES products(code),
  quantity INTEGER NOT NULL,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);