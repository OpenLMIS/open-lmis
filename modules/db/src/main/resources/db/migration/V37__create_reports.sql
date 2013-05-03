-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE report_templates (
  id            SERIAL PRIMARY KEY,
  name          VARCHAR     NOT NULL,
  data          BYTEA       NOT NULL,
  parameters    VARCHAR,
  modifiedBy    INTEGER,
  modifiedDate  TIMESTAMP DEFAULT NOW()
);

CREATE UNIQUE INDEX uc_report_templates_name ON report_templates(LOWER(name));
