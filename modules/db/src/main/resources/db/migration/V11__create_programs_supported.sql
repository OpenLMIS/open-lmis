-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE programs_supported (
  id           SERIAL PRIMARY KEY,
  facilityId   INTEGER NOT NULL REFERENCES facilities (id),
  programId    INTEGER NOT NULL REFERENCES programs (id),
  startDate    TIMESTAMP,
  active       BOOLEAN NOT NULL,
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (facilityId, programId)
);

CREATE INDEX i_program_supported_facilityId ON programs_supported(facilityId);
