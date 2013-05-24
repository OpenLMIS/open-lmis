-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS requisitions;
CREATE TABLE requisitions (
  id                              SERIAL PRIMARY KEY,
  facilityId                      INTEGER     NOT NULL REFERENCES facilities (id),
  programId                       INTEGER     NOT NULL REFERENCES programs (id),
  periodId                        INTEGER     NOT NULL REFERENCES processing_periods (id),
  status                          VARCHAR(20) NOT NULL,
  fullSupplyItemsSubmittedCost    NUMERIC(15, 4),
  nonFullSupplyItemsSubmittedCost NUMERIC(15, 4),
  supervisoryNodeId               INTEGER     REFERENCES supervisory_nodes (id),
  supplyingFacilityId             INTEGER REFERENCES facilities(id),
  submittedDate                   TIMESTAMP,
  createdBy                       INTEGER,
  createdDate                     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                      INTEGER,
  modifiedDate                    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (facilityId, programId, periodId)
);

CREATE INDEX i_requisitions_status ON requisitions(LOWER(status));
CREATE INDEX i_requisitions_programId_supervisoryNodeId ON requisitions(programId, supervisoryNodeId);