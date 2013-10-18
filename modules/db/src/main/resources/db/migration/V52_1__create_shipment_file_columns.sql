--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

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
(name, dataFieldLabel, position, include, mandatory, datePattern, createdBy) VALUES
('orderId', 'header.order.number', 1, TRUE, TRUE, null, (select id from users where userName = 'Admin123')),
('productCode', 'header.product.code', 2, TRUE, TRUE, null, (select id from users where userName = 'Admin123')),
('quantityShipped', 'header.quantity.shipped', 3, TRUE, TRUE, null, (select id from users where userName = 'Admin123')),
('cost', 'header.cost', 4, FALSE, FALSE, null, (select id from users where userName = 'Admin123')),
('packedDate', 'header.packed.date', 5, FALSE, FALSE, 'dd/MM/yy', (select id from users where userName = 'Admin123')),
('shippedDate', 'header.shipped.date', 6, FALSE, FALSE, 'dd/MM/yy', (select id from users where userName = 'Admin123'));
