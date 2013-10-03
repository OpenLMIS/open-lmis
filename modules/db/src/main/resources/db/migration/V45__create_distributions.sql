--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

DROP TABLE IF EXISTS distributions;
CREATE TABLE distributions (
  id             SERIAL PRIMARY KEY,
  deliveryZoneId INTEGER REFERENCES delivery_zones (id)  NOT NULL,
  programId      INTEGER REFERENCES programs (id)        NOT NULL,
  periodId       INTEGER REFERENCES processing_periods (id)         NOT NULL,
  status         VARCHAR(50),
  createdBy      INTEGER                                 NOT NULL REFERENCES users (id),
  createdDate    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy     INTEGER                                 NOT NULL REFERENCES users (id),
  modifiedDate   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uc_dz_program_period ON distributions (deliveryZoneId, programId, periodId);
