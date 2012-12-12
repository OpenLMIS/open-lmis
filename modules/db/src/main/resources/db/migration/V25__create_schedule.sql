CREATE TABLE schedule (
id SERIAL PRIMARY KEY,
code VARCHAR(50) UNIQUE NOT NULL,
name VARCHAR(50) NOT NULL,
description VARCHAR(250)
);

CREATE UNIQUE INDEX uc_schedule_code ON schedule(LOWER(code));