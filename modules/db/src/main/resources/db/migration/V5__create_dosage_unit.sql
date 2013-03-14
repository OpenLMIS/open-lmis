DROP TABLE IF EXISTS dosage_units;
CREATE TABLE dosage_units (
    id SERIAL PRIMARY KEY,
    code varchar(20),
    displayOrder INTEGER
);

