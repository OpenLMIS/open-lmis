CREATE TABLE kit_products_relation (
  kitId INTEGER NOT NULL REFERENCES products(id),
  productId INTEGER NOT NULL REFERENCES products(id),
  quantity INTEGER NOT NULL
);