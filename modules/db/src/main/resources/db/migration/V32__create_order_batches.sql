DROP TABLE IF EXISTS order_batches;
CREATE TABLE order_batches (
  id                             SERIAL PRIMARY KEY,
  createdByUserId                INTEGER,
  createTimeStamp                TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);