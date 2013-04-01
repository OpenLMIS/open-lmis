CREATE TABLE programs (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(50),
    description VARCHAR(50),
    budgetingApplies BOOLEAN,
    usesDar BOOLEAN,
    active BOOLEAN,
    templateConfigured BOOLEAN,
    lastModifiedBy INTEGER,
    lastModifiedDate DATE,
    createdBy INTEGER,
    createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);