-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE requisition_line_item_losses_adjustments (
  requisitionLineItemId         INTEGER REFERENCES requisition_line_items(id),
  type                          VARCHAR(250) REFERENCES losses_adjustments_types(name),
  quantity                      INTEGER,
  PRIMARY KEY(requisitionLineItemId, type)
);