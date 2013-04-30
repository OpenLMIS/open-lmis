-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE processing_schedules (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) UNIQUE NOT NULL,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(250),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT NOW(),
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT NOW()
);
