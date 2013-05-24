-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE role_rights (
  roleId INT REFERENCES roles(id) NOT NULL,
  rightName VARCHAR REFERENCES rights(name) NOT NULL,
  CONSTRAINT unique_role_right UNIQUE (roleId, rightName),
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
