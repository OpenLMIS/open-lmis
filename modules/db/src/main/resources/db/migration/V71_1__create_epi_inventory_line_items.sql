--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

CREATE TABLE epi_inventory_line_items (
  id                SERIAL PRIMARY KEY,
  epiInventoryId    INTEGER REFERENCES epi_inventory (id) NOT NULL,
  productName       VARCHAR(250),
  idealQuantity     NUMERIC(7),
  existingQuantity  NUMERIC(7),
  spoiledQuantity   NUMERIC(7),
  deliveredQuantity NUMERIC(7)
);
