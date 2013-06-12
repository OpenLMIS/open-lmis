-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE requisition_groups (
  id serial PRIMARY KEY,
  code varchar(50) NOT NULL UNIQUE,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(250),
  supervisoryNodeId INTEGER REFERENCES supervisory_nodes(id),
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX i_requisition_group_supervisoryNodeId ON requisition_groups(supervisoryNodeId);

CREATE UNIQUE INDEX uc_requisition_groups_lower_code ON requisition_groups(LOWER(code));
