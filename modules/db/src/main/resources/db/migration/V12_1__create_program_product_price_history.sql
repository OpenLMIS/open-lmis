DROP TABLE IF EXISTS program_product_price_history;
CREATE TABLE program_product_price_history (
    id SERIAL PRIMARY KEY,
    programProductId INTEGER REFERENCES program_products(id) NOT NULL,
    price NUMERIC(20,2) DEFAULT 0,
    pricePerDosage NUMERIC(20,2) DEFAULT 0,
    source VARCHAR(50),
    startDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    endDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modifiedBy VARCHAR(50),
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
