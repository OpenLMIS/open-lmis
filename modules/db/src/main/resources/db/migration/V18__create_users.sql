CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  user_name VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(128) NOT NULL,
  facility_id INT,
  role VARCHAR(50),
  FOREIGN KEY (facility_id) REFERENCES facility(id)
);