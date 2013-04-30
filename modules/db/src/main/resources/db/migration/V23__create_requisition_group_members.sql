-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE requisition_group_members (
  id SERIAL PRIMARY KEY,
  requisitionGroupId INT NOT NULL REFERENCES requisition_groups(id),
  facilityId INT NOT NULL REFERENCES facilities(id),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT NOW(),
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT NOW(),
  UNIQUE (requisitionGroupId, facilityId)
);

CREATE INDEX i_requisition_group_member_facilityId ON requisition_group_members(facilityId);