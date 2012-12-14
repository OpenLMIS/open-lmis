DROP TABLE IF EXISTS facility_operator;
CREATE TABLE facility_operator   (
   id  SERIAL PRIMARY KEY,
   code varchar NOT NULL,
   text varchar(20),
   DisplayOrder INTEGER
);

CREATE UNIQUE INDEX uc_facility_operator ON facility_operator(LOWER(code));
