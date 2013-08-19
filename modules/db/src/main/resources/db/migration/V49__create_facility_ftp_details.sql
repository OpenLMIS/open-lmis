/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
DROP TABLE IF EXISTS facility_ftp_details;
CREATE TABLE facility_ftp_details (
  id SERIAL PRIMARY KEY,
  facilityCode VARCHAR(50) UNIQUE NOT NULL,
  serverHost VARCHAR NOT NULL,
  serverPort VARCHAR NOT NULL,
  userName VARCHAR NOT NULL,
  password VARCHAR NOT NULL,
  localFolderPath VARCHAR NOT NULL,
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);