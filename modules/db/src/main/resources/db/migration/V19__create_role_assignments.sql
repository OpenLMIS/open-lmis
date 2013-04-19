-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE role_assignments (
  userId INTEGER REFERENCES users(id) NOT NULL,
  roleId INTEGER REFERENCES roles(id) NOT NULL,
  programId INTEGER REFERENCES programs(id),
  supervisoryNodeId INTEGER REFERENCES supervisory_nodes(id),

  CONSTRAINT unique_role_assignment UNIQUE (userId, roleId, programId, supervisoryNodeId)
);