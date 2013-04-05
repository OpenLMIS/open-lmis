-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE VENDORS (
id SERIAL PRIMARY KEY,
name VARCHAR(250) NOT NULL,
authToken UUID NOT NULL,
active BOOLEAN
);

CREATE UNIQUE INDEX ucName ON VENDORS(LOWER(name));
