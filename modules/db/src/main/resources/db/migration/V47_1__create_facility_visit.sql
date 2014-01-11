--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

DROP TABLE IF EXISTS facility_visits;
CREATE TABLE facility_visits (
  id               SERIAL PRIMARY KEY,
  distributionId   INTEGER REFERENCES distributions (id),
  facilityId       INTEGER REFERENCES facilities (id),
  confirmedByName  VARCHAR(50),
  confirmedByTitle VARCHAR(50),
  verifiedByName   VARCHAR(50),
  verifiedByTitle  VARCHAR(50),
  observations     TEXT,
  createdBy        INTEGER,
  createdDate      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  UNIQUE (distributionId, facilityId)
);

