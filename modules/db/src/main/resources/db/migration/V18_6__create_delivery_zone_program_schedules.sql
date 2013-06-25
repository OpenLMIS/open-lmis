-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE delivery_zone_program_schedules (
  id SERIAL PRIMARY KEY,
  deliveryZoneId INTEGER REFERENCES delivery_zones(id) NOT NULL,
  programId INTEGER REFERENCES programs(id) NOT NULL,
  scheduleId INTEGER REFERENCES processing_schedules(id) NOT NULL,
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (deliveryZoneId, programId)
);

CREATE INDEX i_delivery_zone_program_schedules_deliveryZoneId ON delivery_zone_program_schedules(deliveryZoneId);