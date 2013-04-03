-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE supply_lines (
  id                    SERIAL PRIMARY KEY,
  description           VARCHAR(250),
  supervisoryNodeId     INTEGER REFERENCES supervisory_nodes(id) NOT NULL,
  programId             INTEGER REFERENCES programs(id) NOT NULL,
  supplyingFacilityId   INTEGER REFERENCES facilities(id) NOT NULL,
  modifiedBy            INTEGER,
  modifiedDate          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy             INTEGER,
  createdDate           TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

  CONSTRAINT unique_supply_line UNIQUE ( supervisoryNodeId , programId , supplyingFacilityId)
);