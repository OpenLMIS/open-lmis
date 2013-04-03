-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE USER_PASSWORD_TOKEN(
   userId                   INTEGER REFERENCES users(id),
   passwordToken            UUID,
   createdDate              TIMESTAMP,
   PRIMARY KEY (userId,passwordToken)
);