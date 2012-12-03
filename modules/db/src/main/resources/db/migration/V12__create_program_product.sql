DROP TABLE IF EXISTS program_product;
CREATE TABLE program_product (
    id SERIAL PRIMARY KEY,
    program_code VARCHAR(50) REFERENCES program(code),
    product_code VARCHAR(50) REFERENCES PRODUCT(code),
    active BOOLEAN,
    modified_by varchar(50),
    modified_date TIMESTAMP,
    UNIQUE (product_code, program_code)
);
