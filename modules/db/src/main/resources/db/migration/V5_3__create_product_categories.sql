-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS product_categories;
CREATE TABLE product_categories (
  id           SERIAL PRIMARY KEY,
  code         VARCHAR(50)  NOT NULL UNIQUE,
  name         VARCHAR(100) NOT NULL,
  displayOrder INTEGER      NOT NULL,
  modifiedBy   INTEGER,
  modifiedDate TIMESTAMP DEFAULT NOW(),
  createdBy    INTEGER,
  createdDate  TIMESTAMP DEFAULT NOW()
);
