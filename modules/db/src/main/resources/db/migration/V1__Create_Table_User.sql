CREATE TABLE open_lmis_user (
    user_name VARCHAR(50) NOT NULL PRIMARY KEY,
    password VARCHAR(128) NOT NULL,
    role VARCHAR(50)  NOT NULL
);