-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE facility_types (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(30) NOT NULL UNIQUE,
  description varchar(250) ,
  levelId INTEGER,
  nominalMaxMonth INTEGER NOT NULL,
  nominalEop NUMERIC(4,2) NOT NULL,
  displayOrder INTEGER,
  active BOOLEAN,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uc_facility_types_lower_code ON facility_types(LOWER(code));

