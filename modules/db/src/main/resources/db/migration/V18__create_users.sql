CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  userName VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(128) NOT NULL,
  facilityId INT REFERENCES facilities(id)
);