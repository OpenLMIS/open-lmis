-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS configurations;
CREATE TABLE configuration_settings (
  id      SERIAL PRIMARY KEY,
  key     VARCHAR(250) NOT NULL,
  value   VARCHAR(250)
);

