-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE comments (
  id  SERIAL PRIMARY KEY,
  rnrId INTEGER NOT NULL REFERENCES requisitions(id),
  commentText VARCHAR(250) NOT NULL,
  createdBy INTEGER NOT NULL REFERENCES users(id),
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER NOT NULL REFERENCES users(id),
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX i_comments_rnrId ON comments(rnrId);