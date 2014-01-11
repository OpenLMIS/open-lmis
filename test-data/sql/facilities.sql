--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

DELETE FROM programs_supported;
DELETE FROM refrigerators;
DELETE FROM facilities;

INSERT INTO facilities
(code, name, description, gln, mainPhone, fax, address1, address2, geographiczoneid, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicSCC, hasElectronicDAR, active, goLiveDate, goDownDate, satellite, comment, enabled, virtualFacility, modifieddate)
VALUES
  ('F10', 'Village Dispensary', 'IT department', 'G7645', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                          id
                                                                                        FROM geographic_zones
                                                                                        WHERE code = 'District1'), 1,
   333, 22.1, 1.2, 3.3, 2, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012',
   'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F11', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'Ngorongoro'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F12', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District2'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F13', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'Ngorongoro'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'FALSE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F14', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'Ngorongoro'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'FALSE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F15', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District1'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F16', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District2'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F17', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District3'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F18', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District1'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F19', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District2'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F20', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District3'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F21', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District1'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F22', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District2'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F23', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District3'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F24', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District1'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F25', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District2'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F26', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District3'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F27', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District1'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F28', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District2'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F29', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District3'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F30', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District1'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F31', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District1'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F32', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District3'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F33', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                      id
                                                                                    FROM geographic_zones
                                                                                    WHERE code =
                                                                                          'District1'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F100', 'Central Hospital', 'IT department', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                       id
                                                                                     FROM geographic_zones
                                                                                     WHERE code =
                                                                                           'District2'), 2, 333, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),

('F8A', 'Facility8A', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                 id
                                                                               FROM geographic_zones
                                                                               WHERE code =
                                                                                     'District1'), 2, 10000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F8B', 'Facility8B', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                 id
                                                                               FROM geographic_zones
                                                                               WHERE code =
                                                                                     'District1'), 2, 20000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F8C', 'Facility8C', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                 id
                                                                               FROM geographic_zones
                                                                               WHERE code =
                                                                                     'District1'), 2, 20000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F9A', 'Facility9A', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                 id
                                                                               FROM geographic_zones
                                                                               WHERE code =
                                                                                     'District2'), 2, 30000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F9B', 'Facility9B', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                 id
                                                                               FROM geographic_zones
                                                                               WHERE code =
                                                                                     'District2'), 2, 40000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F9C', 'Facility9C', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                 id
                                                                               FROM geographic_zones
                                                                               WHERE code =
                                                                                     'District2'), 2, 40000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F10A', 'Facility10A', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District3'), 2, 50000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F10B', 'Facility10B', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District3'), 2, 60000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F10C', 'Facility10C', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District3'), 2, 70000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F11A', 'Facility11A', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District4'), 2, 80000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F11B', 'Facility11B', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District4'), 2, 90000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F11C', 'Facility11C', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District4'), 2, 10000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F12A', 'Facility12A', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District5'), 2, 20000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F12B', 'Facility12B', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District5'), 2, 30000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F12C', 'Facility12C', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District5'), 2, 40000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F13A', 'Facility13A', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District6'), 2, 50000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F13B', 'Facility13B', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District6'), 2, 60000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F13C', 'Facility13C', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District6'), 2, 70000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F14A', 'Facility14A', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District7'), 2, 80000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F14B', 'Facility14B', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District7'), 2, 90000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F14C', 'Facility14C', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District7'), 2, 10000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F15A', 'Facility15A', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District8'), 2, 20000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F15B', 'Facility15B', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District8'), 2, 30000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F15C', 'Facility15C', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District8'), 2, 40000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F16A', 'Facility16A', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District9'), 2, 50000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
('F16B', 'Facility16B', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                   id
                                                                                 FROM geographic_zones
                                                                                 WHERE code =
                                                                                       'District9'), 2, 60000, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),
  ('F16C', 'Facility16C', 'Village Clinic', 'G7646', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                     id
                                                                                   FROM geographic_zones
                                                                                   WHERE code = 'District9'), 2, 70000,
   22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012',
   'FALSE', 'fc', 'TRUE', 'FALSE', '11/11/2012'),

  ('W15', 'Warehouse15', 'Warehouse Province Norte', 'G7646', 9876234981, 'fax', 'A', 'B', 4, 2, 0, 22.3, 1.2, 3.3, 3,
   9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE',
   'FALSE', '11/11/2012'),
  ('W16', 'Warehouse16', 'Warehouse Province Centro', 'G7646', 9876234981, 'fax', 'A', 'B', 4, 2, 0, 22.3, 1.2, 3.3, 3,
   9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE',
   'FALSE', '11/11/2012'),
  ('W17', 'Warehouse17', 'Warehouse Province Sul', 'G7646', 9876234981, 'fax', 'A', 'B', 4, 2, 0, 22.3, 1.2, 3.3, 3,
   9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE',
   'FALSE', '11/11/2012'),
  ('W18', 'Warehouse18', 'Backup Warehouse Province Sul', 'G7646', 9876234981, 'fax', 'A', 'B', 4, 2, 0, 22.3, 1.2, 3.3,
   3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012', 'FALSE', 'fc', 'TRUE',
   'FALSE', '11/11/2012');

INSERT INTO facilities
(code, name, description, gln, mainPhone, fax, address1, address2, geographiczoneid, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicSCC, hasElectronicDAR, active, goLiveDate, goDownDate, satellite, comment, enabled, virtualFacility, parentfacilityid, modifieddate)
VALUES
  ('V10', 'Virtual Facility1', 'Virtual IT department', 'G7645', 9876234981, 'fax', 'A', 'B', (SELECT
                                                                                                 id
                                                                                               FROM geographic_zones
                                                                                               WHERE
                                                                                                 code = 'District1'), 1,
   NULL, 22.3, 1.2, 3.3, 3, 9.9, 6.6, 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', 'TRUE', '11/11/12', '11/11/2012',
   'FALSE', 'fc', 'TRUE', 'TRUE', (SELECT
                                     id
                                   FROM facilities
                                   WHERE code = 'F10'), '11/11/2012');


INSERT INTO programs_supported (facilityId, programId, startDate, active, modifiedBy) VALUES
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F10'), 3, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F10'), 2, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F10'), 4, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F10'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F11'), 3, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F11'), 2, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F11'), 4, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F11'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F12'), 1, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F12'), 2, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F12'), 3, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F13'), 1, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F13'), 2, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F13'), 3, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F14'), 1, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F14'), 2, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F100'), 2, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F100'), 4, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F100'), 3, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F100'), 1, '11/11/12', TRUE, 1),

  ((SELECT
      id
    FROM facilities
    WHERE code = 'F8A'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F8B'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F8C'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F9A'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F9B'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F9C'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F10A'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F10B'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F10C'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F11A'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F11B'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F11C'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F12A'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F12B'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F12C'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F13A'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F13B'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F13C'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F14A'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F14B'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F14C'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F15A'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F15B'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F15C'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F16A'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F16B'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'F16C'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'W15'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'W16'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'W17'), 5, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'W18'), 5, '11/11/12', TRUE, 1),

  ((SELECT
      id
    FROM facilities
    WHERE code = 'V10'), 3, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'V10'), 2, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'V10'), 4, '11/11/12', TRUE, 1),
  ((SELECT
      id
    FROM facilities
    WHERE code = 'V10'), 5, '11/11/12', TRUE, 1);

INSERT INTO refrigerators (brand, model, serialNumber, facilityId, createdBy, modifiedBy) VALUES
  ('SAM', 'SAM1', 'SER11', (SELECT
                              id
                            FROM facilities
                            WHERE code = 'F10'), 1, 1);

INSERT INTO refrigerators (brand, model, serialNumber, facilityId, createdBy, modifiedBy) VALUES
  ('SAM', 'SAM2', 'SER12', (SELECT
                              id
                            FROM facilities
                            WHERE code = 'F10'), 1, 1);