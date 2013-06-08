-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS requisition_status_changes;
CREATE TABLE requisition_status_changes (
  id                              SERIAL PRIMARY KEY,
  rnrId                           INTEGER     NOT NULL REFERENCES requisitions (id),
  status                          VARCHAR(20) NOT NULL,
  createdBy                       INTEGER NOT NULL REFERENCES users(id),
  createdDate                     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                      INTEGER NOT NULL REFERENCES users(id),
  modifiedDate                    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
