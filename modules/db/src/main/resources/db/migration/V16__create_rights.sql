-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE rights (
  name VARCHAR(200) PRIMARY KEY,
  rightType VARCHAR(20) NOT NULL,
  description VARCHAR(200),
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);