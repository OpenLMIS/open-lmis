-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE orders (
  id                  SERIAL PRIMARY KEY,
  requisitionId       INTEGER NOT NULL REFERENCES requisitions (id),
  shipmentId          INTEGER,
  fulfilled           BOOLEAN,
  createdDate         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy           INTEGER NOT NULL REFERENCES users (id)
);