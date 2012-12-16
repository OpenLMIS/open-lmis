DROP TABLE IF EXISTS facility_operators;
CREATE TABLE facility_operators   (
   id  SERIAL PRIMARY KEY,
   code varchar NOT NULL,
   text varchar(20),
   DisplayOrder INTEGER
);

CREATE UNIQUE INDEX uc_facility_operators ON facility_operators(LOWER(code));
