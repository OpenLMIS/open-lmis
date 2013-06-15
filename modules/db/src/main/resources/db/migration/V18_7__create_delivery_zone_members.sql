-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE delivery_zone_members (
  id SERIAL PRIMARY KEY,
  deliveryZoneId INT NOT NULL REFERENCES delivery_zones(id),
  facilityId INT NOT NULL REFERENCES facilities(id),
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (deliveryZoneId, facilityId)
);

CREATE INDEX i_delivery_zone_members_facilityId ON delivery_zone_members(facilityId);