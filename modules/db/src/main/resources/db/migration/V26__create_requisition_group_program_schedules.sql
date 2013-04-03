-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE requisition_group_program_schedules (
  requisitionGroupId INTEGER REFERENCES requisition_groups(id),
  programId INTEGER REFERENCES programs(id),
  scheduleId INTEGER REFERENCES processing_schedules(id),
  directDelivery BOOLEAN NOT NULL,
  dropOffFacilityId INTEGER REFERENCES facilities(id),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  createdBy INTEGER,
  PRIMARY KEY (requisitionGroupId, programId)
);