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
  email VARCHAR(50),
  supervisorId INTEGER references users(id),
  facilityId INT REFERENCES facilities(id),
  active BOOLEAN DEFAULT FALSE,
  vendorId INTEGER references vendors(id),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  createdBy INTEGER,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

  constraint email_not_null check
    (
      vendorId IS NOT NULL
      OR (vendorId IS NULL AND email IS NOT NULL)
    )
);

CREATE UNIQUE INDEX uc_users_userName_vendor ON users(LOWER(userName), vendorId);
CREATE INDEX i_users_firstName_lastName_email ON users(LOWER(firstName), LOWER(lastName), LOWER(email));
CREATE UNIQUE INDEX uc_users_email ON users(LOWER(email));
CREATE UNIQUE INDEX uc_users_employeeId ON users(LOWER(employeeId));
