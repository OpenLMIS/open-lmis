--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

ALTER TABLE vaccination_child_coverage_line_items
ADD COLUMN displayOrder INTEGER;


UPDATE vaccination_child_coverage_line_items
SET displayOrder = 0
WHERE vaccination = 'BCG';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 1
WHERE vaccination = 'Polio (Newborn)';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 2
WHERE vaccination = 'Polio 1a dose';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 3
WHERE vaccination = 'Polio 2a dose';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 4
WHERE vaccination = 'Polio 3a dose';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 5
WHERE vaccination = 'IPV';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 6
WHERE vaccination = 'Penta 1a dose';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 7
WHERE vaccination = 'Penta 2a dose';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 8
WHERE vaccination = 'Penta 3a dose';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 9
WHERE vaccination = 'PCV10 1a dose';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 10
WHERE vaccination = 'PCV10 2a dose';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 11
WHERE vaccination = 'PCV10 3a dose';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 12
WHERE vaccination = 'RV Rotarix 1a dose';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 13
WHERE vaccination = 'RV Rotarix 2a dose';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 14
WHERE vaccination = 'Sarampo 1a dose';

UPDATE vaccination_child_coverage_line_items
SET displayOrder = 15
WHERE vaccination = 'Sarampo 2a dose';
