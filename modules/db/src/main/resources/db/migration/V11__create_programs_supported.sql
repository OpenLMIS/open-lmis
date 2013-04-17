-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE programs_supported (
  id           SERIAL PRIMARY KEY,
  facilityId   INTEGER REFERENCES facilities (id),
  programId    INTEGER REFERENCES programs (id),
  startDate    TIMESTAMP,
  active       BOOLEAN NOT NULL,
  modifiedBy   INTEGER,
  modifiedDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  createdBy INTEGER,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  UNIQUE (facilityId, programId)
);

CREATE INDEX uc_program_supported_facilityId ON programs_supported(facilityId);
CREATE INDEX uc_program_supported_facilityId_programId ON programs_supported(facilityId, programId);