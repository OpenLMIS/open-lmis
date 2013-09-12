-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
DROP TABLE IF EXISTS order_file_columns;

CREATE TABLE order_file_columns (
  id                 SERIAL PRIMARY KEY,
  dataFieldLabel     VARCHAR(50),
  nested             VARCHAR(50),
  keyPath            VARCHAR(50),
  includeInOrderFile BOOLEAN NOT NULL DEFAULT TRUE,
  columnLabel        VARCHAR(50),
  format             VARCHAR(20),
  position           INTEGER NOT NULL,
  openLmisField      BOOLEAN NOT NULL DEFAULT FALSE,
  createdBy          INTEGER,
  createdDate        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy         INTEGER,
  modifiedDate       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO order_file_columns
(position, openLmisField, dataFieldLabel, nested, keyPath, columnLabel, format) VALUES
(1, TRUE, 'header.order.number', 'order', 'id', 'Order number', ''),
(2, TRUE, 'create.facility.code', 'order', 'rnr/facility/code', 'Facility code', ''),
(3, TRUE, 'header.product.code', 'lineItem', 'productCode', 'Product code', ''),
(4, TRUE, 'header.quantity.approved', 'lineItem', 'quantityApproved', 'Approved quantity', ''),
(5, TRUE, 'label.period', 'order', 'rnr/period/startDate', 'Period', 'MM/yy'),
(6, TRUE, 'header.order.date', 'order', 'createdDate', 'Order date', 'dd/MM/yy');
