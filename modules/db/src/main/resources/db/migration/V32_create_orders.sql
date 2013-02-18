DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  id                             SERIAL PRIMARY KEY,
  orderedBy                      VARCHAR(50),
  orderedDate                    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);