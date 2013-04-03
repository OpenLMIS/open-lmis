-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE roles (
  id SERIAL PRIMARY KEY,
  adminRole BOOLEAN NOT NULL,
  name VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(250),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INTEGER,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE UNIQUE INDEX unique_role_name ON roles(LOWER(name));
