--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

CREATE TABLE supply_lines (
  id                  SERIAL PRIMARY KEY,
  description         VARCHAR(250),
  supervisoryNodeId   INTEGER REFERENCES supervisory_nodes (id) NOT NULL,
  programId           INTEGER REFERENCES programs (id)          NOT NULL,
  supplyingFacilityId INTEGER REFERENCES facilities (id)        NOT NULL,
  exportOrders        BOOLEAN                                   NOT NULL,
  createdBy           INTEGER,
  createdDate         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy          INTEGER,
  modifiedDate        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT unique_supply_line UNIQUE (supervisoryNodeId, programId)
);

