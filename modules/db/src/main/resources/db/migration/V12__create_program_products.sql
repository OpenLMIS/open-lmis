DROP TABLE IF EXISTS program_products;
CREATE TABLE program_products (
    id SERIAL PRIMARY KEY,
    programId INTEGER REFERENCES program(id) NOT NULL,
    productId INTEGER REFERENCES product(id) NOT NULL,
    dosesPerMonth INTEGER NOT NULL,
    active BOOLEAN,
    modifiedBy VARCHAR(50),
    modifiedDate TIMESTAMP,
    UNIQUE (productId, programId)
);
