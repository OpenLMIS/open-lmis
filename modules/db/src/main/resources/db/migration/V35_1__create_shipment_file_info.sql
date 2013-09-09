-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE shipment_file_info (
  id              SERIAL PRIMARY KEY,
  fileName        VARCHAR(200) NOT NULL,
  processingError BOOLEAN      NOT NULL,
  modifiedDate    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

