--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

DELETE FROM regimens;

INSERT INTO regimens (programid, categoryid, code, name, active, displayorder) VALUES
(2, 1, '001', 'REGIMEN1', TRUE, 1),
(2, 1, '002', 'REGIMEN2', TRUE, 2),
(2, 1, '003', 'REGIMEN3', TRUE, 3),
(2, 1, '004', 'REGIMEN4', FALSE, 4),
(2, 2, '005', 'REGIMEN5', TRUE, 5),
(2, 2, '006', 'REGIMEN6', TRUE, 6),
(2, 2, '007', 'REGIMEN7', FALSE, 7);


DELETE FROM program_regimen_columns;

INSERT INTO program_regimen_columns(name, programId, label, visible, dataType) values
('code',2, 'header.code',true,'regimen.reporting.dataType.text'),
('name',2,'header.name',true,'regimen.reporting.dataType.text'),
('patientsOnTreatment',2,'Number of patients on treatment',true,'regimen.reporting.dataType.numeric'),
('patientsToInitiateTreatment',2,'Number of patients to be initiated treatment',true,'regimen.reporting.dataType.numeric'),
('patientsStoppedTreatment',2,'Number of patients stopped treatment',true,'regimen.reporting.dataType.numeric'),
('remarks',2,'Remarks',true,'regimen.reporting.dataType.text');
