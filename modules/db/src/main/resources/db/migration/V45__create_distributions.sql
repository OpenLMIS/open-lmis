-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS distributions;
CREATE TABLE distributions (
  id             SERIAL PRIMARY KEY,
  deliveryZoneId INTEGER REFERENCES delivery_zones (id)  NOT NULL,
  programId      INTEGER REFERENCES programs (id)        NOT NULL,
  periodId       INTEGER REFERENCES processing_periods (id)         NOT NULL,
  status         VARCHAR(50),
  createdBy      INTEGER                                 NOT NULL REFERENCES users (id),
  createdDate    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy     INTEGER                                 NOT NULL REFERENCES users (id),
  modifiedDate   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uc_dz_program_period ON distributions (deliveryZoneId, programId, periodId);
