-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE processing_periods (
  id SERIAL PRIMARY KEY,
  scheduleId INTEGER REFERENCES processing_schedules(id) NOT NULL,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(250),
  startDate TIMESTAMP NOT NULL,
  endDate TIMESTAMP NOT NULL,
  numberOfMonths INTEGER,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INTEGER,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE UNIQUE INDEX uc_period_name ON processing_periods(LOWER(name), scheduleId);