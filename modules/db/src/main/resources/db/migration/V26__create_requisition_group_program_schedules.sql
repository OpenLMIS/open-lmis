-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE requisition_group_program_schedules (
  id SERIAL PRIMARY KEY,
  requisitionGroupId INTEGER REFERENCES requisition_groups(id) NOT NULL,
  programId INTEGER REFERENCES programs(id) NOT NULL,
  scheduleId INTEGER REFERENCES processing_schedules(id) NOT NULL,
  directDelivery BOOLEAN NOT NULL,
  dropOffFacilityId INTEGER REFERENCES facilities(id),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  createdBy INTEGER,
  UNIQUE (requisitionGroupId, programId)
);

CREATE INDEX i_requisition_group_program_schedules_requisitionGroupId ON requisition_group_program_schedules(requisitionGroupId);