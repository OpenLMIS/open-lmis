DROP TABLE IF EXISTS program_product;
CREATE TABLE program_product (
    id SERIAL PRIMARY KEY,
    program_code VARCHAR(50) REFERENCES program(code),
    product_id INTEGER REFERENCES PRODUCT(id),
    active BOOLEAN,
    modified_by varchar(50),
    modified_date TIMESTAMP,
    UNIQUE (product_id, program_code)
);
