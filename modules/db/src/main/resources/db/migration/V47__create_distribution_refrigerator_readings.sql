--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

DROP TABLE IF EXISTS distribution_refrigerator_readings;
CREATE TYPE radio_options AS ENUM ('Y', 'N', 'D');

CREATE TABLE distribution_refrigerator_readings (
  id                       SERIAL PRIMARY KEY,
  temperature              NUMERIC(3, 1),
  functioningCorrectly     radio_options,
  lowAlarmEvents           INTEGER,
  highAlarmEvents          INTEGER,
  problemSinceLastTime     radio_options,
  notes                    VARCHAR(30),
  refrigeratorSerialNumber VARCHAR(30) REFERENCES refrigerators (serialNumber),
  facilityId               INTEGER REFERENCES facilities (id),
  distributionId           INTEGER REFERENCES distributions (id),
  createdBy                INTEGER NOT NULL REFERENCES users (id),
  createdDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy               INTEGER NOT NULL REFERENCES users (id),
  modifiedDate             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uc_refrigerator_distribution UNIQUE (refrigeratorSerialNumber, facilityId, distributionId)
);

