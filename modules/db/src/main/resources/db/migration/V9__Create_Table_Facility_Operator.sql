DROP TABLE IF EXISTS facility_operator;
CREATE TABLE facility_operator   (
   id SERIAL PRIMARY KEY,
   text varchar(20),
   display_order INTEGER
);