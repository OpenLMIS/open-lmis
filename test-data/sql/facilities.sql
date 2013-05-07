-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DELETE FROM programs_supported;
DELETE FROM facilities;

INSERT INTO facilities
(code, name, description, gln, mainPhone, fax, address1, address2, geographiczoneid, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicScc, hasElectronicDar, active, goLiveDate, goDownDate, satellite, comment, dataReportable, modifieddate) values
('F10','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',4,1,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F11','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F12','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F13','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F14','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F15','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F16','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F17','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F18','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F19','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F20','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F21','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F22','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F23','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F24','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F25','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F26','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F27','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F28','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F29','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F30','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F31','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F32','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F33','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012');




INSERT INTO programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES
((SELECT id FROM facilities WHERE code = 'F10'), 3, '11/11/12', true, 1),
((SELECT id FROM facilities WHERE code = 'F10'), 2, '11/11/12', true, 1),
((SELECT id FROM facilities WHERE code = 'F11'), 3, '11/11/12', true, 1),
((SELECT id FROM facilities WHERE code = 'F11'), 2, '11/11/12', true, 1);
