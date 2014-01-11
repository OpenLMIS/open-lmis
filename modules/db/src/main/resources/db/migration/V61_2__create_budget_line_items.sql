--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

CREATE TABLE budget_line_items (
  id              SERIAL PRIMARY KEY,
  facilityCode    VARCHAR(50)    NOT NULL REFERENCES facilities (code),
  programCode     VARCHAR(50)    NOT NULL REFERENCES programs (code),
  periodId        INTEGER        NOT NULL REFERENCES processing_periods (id),
  budgetFileId    INTEGER        NOT NULL REFERENCES budget_file_info (id),
  periodDate      TIMESTAMP      NOT NULL,
  allocatedBudget NUMERIC(20, 2) NOT NULL,
  notes           VARCHAR(255),
  createdBy       INTEGER,
  createdDate     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy      INTEGER,
  modifiedDate    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (facilityCode, programCode, periodId)
);
