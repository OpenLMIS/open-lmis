--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--


DELETE FROM facility_types;
INSERT INTO facility_types (code, name, description, levelId, nominalMaxMonth, nominalEop, displayOrder, active)
VALUES
('warehouse','Warehouse', 'Central Supply Depot',null,3,0.5, 11, TRUE),
('lvl3_hospital','Lvl3 Hospital', 'State Hospital',null,3,0.5,1, TRUE),
('lvl2_hospital','Lvl2 Hospital', 'Regional Hospital',null,3,0.5,2, TRUE),
('state_office','State Office', 'Management Office, no patient services',null,3,0.5,9, TRUE),
('district_office','District Office', 'Management Office, no patient services',null,3,0.5,10, TRUE),
('health_center','Health Center', 'Multi-program clinic',null,3,0.5,4, TRUE),
('health_post','Health Post', 'Community Clinic',null,3,0.5,5, TRUE),
('lvl1_hospital','Lvl1 Hospital', 'District Hospital',null,3,0.5,3, TRUE),
('satellite_facility','Satellite Facility', 'Temporary service delivery point',null,1,0.25,6, FALSE),
('chw','CHW', 'Mobile worker based out of health center',null,1,0.25,7, TRUE),
('dhmt','DHMT', 'District Health Management Team',null,3,0.5,8, TRUE);
