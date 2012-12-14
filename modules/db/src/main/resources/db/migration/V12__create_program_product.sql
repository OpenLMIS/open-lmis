DROP TABLE IF EXISTS program_product;
CREATE TABLE program_product (
    id SERIAL PRIMARY KEY,
    program_id INTEGER REFERENCES program(id) NOT NULL,
    product_id INTEGER REFERENCES product(id) NOT NULL,
    doses_per_month INTEGER NOT NULL,
    active BOOLEAN,
    modified_by VARCHAR(50),
    modified_date TIMESTAMP,
    UNIQUE (product_id, program_id)
);
