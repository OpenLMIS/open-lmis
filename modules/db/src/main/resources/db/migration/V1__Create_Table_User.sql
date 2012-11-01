CREATE TABLE Open_LMIS_User (
    user_name varchar(50) NOT NULL PRIMARY KEY,
    password varchar(128) NOT NULL,
    role varchar(50)  NOT NULL
);