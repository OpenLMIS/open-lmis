-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

INSERT INTO delivery_zones ( code ,name)values
('DZ1','Delivery Zone First'),
('DZ2','Delivery Zone Second');

INSERT INTO delivery_zone_members ( deliveryZoneId ,facilityId )values
((select id from  delivery_zones where code ='DZ1'),(select id from  facilities where code ='F10')),
((select id from  delivery_zones where code ='DZ2'),(select id from  facilities where code ='F11'));

INSERT INTO delivery_zone_program_schedules
(deliveryZoneId, programId, scheduleId ) values(
(select id from delivery_zones where code='DZ1'),
(select id from programs where code='VACCINES'),
(select id from processing_schedules where code='M')
);