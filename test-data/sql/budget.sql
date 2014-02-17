--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

INSERT INTO budget_file_info (filename, processingError) VALUES ('abc.csv', false);

INSERT INTO budget_line_items (periodId, budgetFileId, facilityId, programId, periodDate, allocatedBudget) VALUES (
  (SELECT
     id
   FROM processing_periods
   WHERE name = 'Dec2013'),
  (SELECT id FROM budget_file_info WHERE id = 1),
  (SELECT id FROM facilities WHERE code = 'F10'),
  (SELECT id FROM programs WHERE code = 'MALARIA'), now(), 123);