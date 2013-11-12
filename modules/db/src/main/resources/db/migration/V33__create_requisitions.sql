--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

DROP TABLE IF EXISTS requisitions;
CREATE TABLE requisitions (
  id                              SERIAL PRIMARY KEY,
  facilityId                      INTEGER     NOT NULL REFERENCES facilities (id),
  programId                       INTEGER     NOT NULL REFERENCES programs (id),
  periodId                        INTEGER     NOT NULL REFERENCES processing_periods (id),
  status                          VARCHAR(20) NOT NULL,
  emergency                       BOOLEAN     NOT NULL DEFAULT FALSE,
  fullSupplyItemsSubmittedCost    NUMERIC(15, 4),
  nonFullSupplyItemsSubmittedCost NUMERIC(15, 4),
  supervisoryNodeId               INTEGER REFERENCES supervisory_nodes (id),
  createdBy                       INTEGER,
  createdDate                     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                      INTEGER,
  modifiedDate                    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX i_requisitions_status ON requisitions (LOWER(status));
CREATE INDEX i_requisitions_programId_supervisoryNodeId ON requisitions (programId, supervisoryNodeId);