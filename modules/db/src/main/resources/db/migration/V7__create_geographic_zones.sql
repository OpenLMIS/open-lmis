-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE geographic_zones (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(250) NOT NULL,
  levelId INTEGER NOT NULL REFERENCES geographic_levels(id),
  parent INTEGER REFERENCES geographic_zones(id),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP,
  createdBy INTEGER,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);