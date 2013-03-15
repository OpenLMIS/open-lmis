CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  userName VARCHAR(50) NOT NULL,
  password VARCHAR(128) NOT NULL DEFAULT 'not-in-use',
  firstName VARCHAR(50) NOT NULL,
  lastName VARCHAR(50) NOT NULL,
  employeeId VARCHAR(50),
  jobTitle VARCHAR(50),
  primaryNotificationMethod VARCHAR(50),
  officePhone VARCHAR(30),
  cellPhone VARCHAR(30),
  email VARCHAR(50) NOT NULL,
  supervisorId INTEGER references users(id),
  facilityId INT REFERENCES facilities(id),
  active BOOLEAN DEFAULT FALSE,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INTEGER,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE UNIQUE INDEX uc_users_userName ON users(LOWER(userName));
CREATE UNIQUE INDEX uc_users_email ON users(LOWER(email));
CREATE UNIQUE INDEX uc_users_employeeId ON users(LOWER(employeeId));