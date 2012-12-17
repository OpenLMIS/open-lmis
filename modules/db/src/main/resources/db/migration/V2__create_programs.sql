CREATE TABLE programs (
    id SERIAL primary key,
    code varchar(50) not null unique,
    name varchar(50),
    description varchar(50),
    budgetingApplies boolean,
    usesDar boolean,
    active boolean,
    lastModifiedDate date,
    lastModifiedBy integer
);