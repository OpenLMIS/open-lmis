-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

INSERT INTO master_regimen_columns values
('code','Code',true,'regimen.reporting.dataType.text'),
('name','Name',true,'regimen.reporting.dataType.text'),
('onTreatment','Number of patients on treatment',true,'regimen.reporting.dataType.numeric'),
('initiatedTreatment','Number of patients to be initiated treatment',true,'regimen.reporting.dataType.numeric'),
('stoppedTreatment','Number of patients stopped treatment',true,'regimen.reporting.dataType.numeric'),
('remarks','Remarks',true,'regimen.reporting.dataType.text');
