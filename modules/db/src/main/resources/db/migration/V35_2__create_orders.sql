--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

CREATE TABLE orders (
  id           INTEGER     NOT NULL UNIQUE REFERENCES requisitions (id),
  shipmentId   INTEGER REFERENCES shipment_file_info (id),
  status       VARCHAR(20) NOT NULL,
  ftpComment   VARCHAR(50),
  supplyLineId INTEGER REFERENCES supply_lines (id),
  createdBy    INTEGER     NOT NULL REFERENCES users (id),
  createdDate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy   INTEGER     NOT NULL REFERENCES users (id),
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);