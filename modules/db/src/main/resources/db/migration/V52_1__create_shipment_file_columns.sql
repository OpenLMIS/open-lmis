-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE shipment_file_columns (
  id             SERIAL PRIMARY KEY,
  name           VARCHAR(150) NOT NULL,
  dataFieldLabel VARCHAR(150),
  position       INTEGER,
  include        BOOLEAN      NOT NULL,
  mandatory      BOOLEAN      NOT NULL,
  datePattern    VARCHAR(25),
  createdBy      INTEGER,
  createdDate    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy     INTEGER,
  modifiedDate   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO shipment_file_columns
(name, dataFieldLabel, position, include, mandatory, datePattern) VALUES
('orderId', 'header.order.number', 1, TRUE, TRUE, null),
('productCode', 'header.product.code', 2, TRUE, TRUE, null),
('quantityShipped', 'header.quantity.shipped', 3, TRUE, TRUE, null),
('cost', 'header.cost', 4, FALSE, FALSE, null),
('packedDate', 'header.packed.date', 5, FALSE, FALSE, 'dd/MM/yy'),
('shippedDate', 'header.shipped.date', 6, FALSE, FALSE, 'dd/MM/yy');
