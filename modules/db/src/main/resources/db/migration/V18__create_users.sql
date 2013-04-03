-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

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
  modifiedDate TIMESTAMP,
  createdBy INTEGER,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE UNIQUE INDEX uc_users_userName ON users(LOWER(userName));
CREATE UNIQUE INDEX uc_users_email ON users(LOWER(email));
CREATE UNIQUE INDEX uc_users_employeeId ON users(LOWER(employeeId));