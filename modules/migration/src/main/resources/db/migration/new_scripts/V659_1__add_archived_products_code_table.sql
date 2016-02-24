CREATE TABLE archived_products(
  id SERIAL PRIMARY KEY,
  facilityId INT NOT NULL REFERENCES facilities(id),
  productCode VARCHAR(50) NOT NULL REFERENCES products(code)
);