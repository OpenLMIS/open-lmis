--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

CREATE TABLE users (
  id                        SERIAL PRIMARY KEY,
  userName                  VARCHAR(50)  NOT NULL,
  password                  VARCHAR(128) NOT NULL DEFAULT 'not-in-use',
  firstName                 VARCHAR(50)  NOT NULL,
  lastName                  VARCHAR(50)  NOT NULL,
  employeeId                VARCHAR(50),
  restrictLogin             BOOLEAN DEFAULT FALSE,
  jobTitle                  VARCHAR(50),
  primaryNotificationMethod VARCHAR(50),
  officePhone               VARCHAR(30),
  cellPhone                 VARCHAR(30),
  email                     VARCHAR(50)  NOT NULL,
  supervisorId              INTEGER REFERENCES users (id),
  facilityId                INT REFERENCES facilities (id),
  verified                  BOOLEAN DEFAULT FALSE,
  active                    BOOLEAN DEFAULT TRUE,
  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uc_users_userName ON users (LOWER(userName));
CREATE INDEX i_users_firstName_lastName_email ON users (LOWER(firstName), LOWER(lastName), LOWER(email));
CREATE UNIQUE INDEX uc_users_email ON users (LOWER(email));
CREATE UNIQUE INDEX uc_users_employeeId ON users (LOWER(employeeId));
