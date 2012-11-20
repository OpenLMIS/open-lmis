DROP TABLE IF EXISTS facility_operator;
CREATE TABLE facility_operator   (
   id INTEGER PRIMARY KEY,
   code varchar UNIQUE,
   text varchar(20),
   display_order INTEGER
);