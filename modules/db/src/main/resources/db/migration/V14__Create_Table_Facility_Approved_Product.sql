DROP TABLE IF EXISTS facility_approved_product;
CREATE TABLE facility_approved_product (
    id SERIAL PRIMARY KEY,
    facility_type_code VARCHAR(50) REFERENCES facility_type(code),
    product_code VARCHAR(50) REFERENCES product(code),
    modified_by varchar(50),
    modified_date TIMESTAMP,
    UNIQUE (facility_type_code, product_code)
);
