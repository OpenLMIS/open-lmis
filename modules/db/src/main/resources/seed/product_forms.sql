--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

DELETE FROM product_forms;
INSERT INTO product_forms(code, displayOrder) VALUES
('Tablet',1),
('Capsule',2),
('Bottle',3),
('Vial',4),
('Ampule',5),
('Drops',6),
('Powder',7),
('Each',8),
('Injectable',9),
('Tube',10),
('Solution',11),
('Inhaler',12),
('Patch',13),
('Implant',14),
('Sachet',15),
('Device',16),
('Other',17);

