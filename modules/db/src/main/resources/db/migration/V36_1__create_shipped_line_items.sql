-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE shipped_line_items (
  id  SERIAL PRIMARY KEY,
  orderId INTEGER NOT NULL REFERENCES orders(id),
  productCode VARCHAR(50) NOT NULL REFERENCES products(code),
  shippedQuantity INTEGER  NOT NULL,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
