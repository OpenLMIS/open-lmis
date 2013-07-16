-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

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
