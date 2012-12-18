DROP TABLE IF EXISTS facility_approved_products;
CREATE TABLE facility_approved_products (
    id SERIAL PRIMARY KEY,
    facilityTypeId INTEGER REFERENCES facility_type(id),
    programProductId INTEGER REFERENCES program_products(id),
    maxMonthsOfStock INTEGER NOT NULL,
    modifiedBy VARCHAR(50),
    modifiedDate TIMESTAMP,
    UNIQUE (facilityTypeId, programProductId)
);
