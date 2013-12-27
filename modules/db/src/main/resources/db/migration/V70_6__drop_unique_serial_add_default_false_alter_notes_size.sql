--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

ALTER TABLE refrigerators DROP CONSTRAINT refrigerators_serialnumber_key;
ALTER TABLE refrigerators ADD CONSTRAINT uc_serialNumber_facilityId_refrigerators UNIQUE (serialNumber, facilityId);

ALTER TABLE refrigerator_readings ALTER COLUMN notes TYPE VARCHAR(255);

ALTER TABLE refrigerator_problems
ALTER COLUMN operatorError SET DEFAULT FALSE,
ALTER COLUMN burnerProblem SET DEFAULT FALSE,
ALTER COLUMN gasLeakage SET DEFAULT FALSE,
ALTER COLUMN egpFault SET DEFAULT FALSE,
ALTER COLUMN thermostatSetting SET DEFAULT FALSE,
ALTER COLUMN other SET DEFAULT FALSE;







