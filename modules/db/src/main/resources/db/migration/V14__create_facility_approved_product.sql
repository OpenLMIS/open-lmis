DROP TABLE IF EXISTS facility_approved_product;
CREATE TABLE facility_approved_product (
    id SERIAL PRIMARY KEY,
    facility_type_id INTEGER REFERENCES facility_type(id),
    product_id INTEGER REFERENCES product(id),
    modified_by varchar(50),
    modified_date TIMESTAMP,
    UNIQUE (facility_type_id, product_id)
);
