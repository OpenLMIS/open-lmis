-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS regimen_line_items;
CREATE TABLE regimen_line_items (
  id                          SERIAL PRIMARY KEY,
  code                        VARCHAR(50),
  name                        VARCHAR(250),
  regimenDisplayOrder         INTEGER,
  regimenCategory             VARCHAR(50),
  regimenCategoryDisplayOrder INTEGER,
  rnrId                       INTEGER NOT NULL REFERENCES requisitions (id),
  patientsOnTreatment         INTEGER,
  patientsToInitiateTreatment INTEGER,
  patientsStoppedTreatment    INTEGER,
  remarks                     VARCHAR(250),
  createdBy                   INTEGER,
  createdDate                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                  INTEGER,
  modifiedDate                TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

