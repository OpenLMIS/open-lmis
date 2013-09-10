-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE shipment_file_columns (
  id             SERIAL PRIMARY KEY,
  name           VARCHAR(150) NOT NULL,
  dataFieldLabel VARCHAR(150),
  position       INTEGER UNIQUE,
  include        BOOLEAN      NOT NULL,
  mandatory      BOOLEAN      NOT NULL,
  datePattern    VARCHAR(25),
  createdBy      INTEGER,
  createdDate    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy     INTEGER,
  modifiedDate   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
