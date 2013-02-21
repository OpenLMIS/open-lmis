DROP TABLE IF EXISTS order_batches;
CREATE TABLE order_batches (
  id                             SERIAL PRIMARY KEY,
  orderedBy                      VARCHAR(50),
  orderedDate                    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);