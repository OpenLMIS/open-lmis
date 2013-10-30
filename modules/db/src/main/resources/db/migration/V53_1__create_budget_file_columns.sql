--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

CREATE TABLE budget_file_columns (
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

INSERT INTO budget_file_columns
(name, dataFieldLabel, position, include, mandatory, datePattern) VALUES
('facilityCode', 'header.facility.code', 1, TRUE, TRUE, null),
('programCode', 'header.program.code', 2, TRUE, TRUE, null),
('periodStartDate', 'header.period.start.date', 3, TRUE, TRUE, 'dd/MM/yy'),
('allocatedBudget', 'header.allocatedBudget', 4, TRUE, TRUE, null),
('notes', 'header.notes', 5, FALSE, FALSE, null);