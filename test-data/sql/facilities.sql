-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DELETE FROM programs_supported;
DELETE FROM facilities;

INSERT INTO facilities
(code, name, description, gln, mainPhone, fax, address1, address2, geographiczoneid, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicScc, hasElectronicDar, active, goLiveDate, goDownDate, satellite, comment, dataReportable, modifieddate) values
('F10','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',4,1,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012'),
('F11','Central Hospital','IT department','G7646',9876234981,'fax','A','B',4,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE','11/11/2012');

INSERT INTO programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES
((SELECT id FROM facilities WHERE code = 'F10'), 3, '11/11/12', true, 1),
((SELECT id FROM facilities WHERE code = 'F10'), 2, '11/11/12', true, 1),
((SELECT id FROM facilities WHERE code = 'F11'), 3, '11/11/12', true, 1),
((SELECT id FROM facilities WHERE code = 'F11'), 2, '11/11/12', true, 1);
