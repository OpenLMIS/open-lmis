--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

ALTER TABLE refrigerator_readings ADD CONSTRAINT uc_refrigeratorId_facilityVisitId_refrigerator_readings UNIQUE (refrigeratorId, facilityVisitId);
ALTER TABLE epi_use_line_items ADD CONSTRAINT uc_productGroupId_facilityVisitId_epi_use_line_items UNIQUE (productGroupId, facilityVisitId);
ALTER TABLE epi_inventory_line_items ADD CONSTRAINT uc_programProductId_facilityVisitId_epi_inventory_line_items UNIQUE (programProductId, facilityVisitId);