-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

INSERT INTO delivery_zones ( code ,name)values
('DZ1','Delivery Zone First'),
('DZ2','Delivery Zone Second'),
('Norte','Norte Province'),
('Centro','Centro Province'),
('Sul','Sul Province');

INSERT INTO delivery_zone_members ( deliveryZoneId ,facilityId )values
((select id from  delivery_zones where code ='DZ1'),(select id from  facilities where code ='F10')),
((select id from  delivery_zones where code ='DZ2'),(select id from  facilities where code ='F11')),

((select id from  delivery_zones where code ='Norte'),(select id from  facilities where code ='F8A')),
((select id from  delivery_zones where code ='Norte'),(select id from  facilities where code ='F8B')),
((select id from  delivery_zones where code ='Norte'),(select id from  facilities where code ='F8C')),
((select id from  delivery_zones where code ='Norte'),(select id from  facilities where code ='F9A')),
((select id from  delivery_zones where code ='Norte'),(select id from  facilities where code ='F9B')),
((select id from  delivery_zones where code ='Norte'),(select id from  facilities where code ='F9C')),
((select id from  delivery_zones where code ='Norte'),(select id from  facilities where code ='F10A')),
((select id from  delivery_zones where code ='Norte'),(select id from  facilities where code ='F10B')),
((select id from  delivery_zones where code ='Norte'),(select id from  facilities where code ='F10C')),

((select id from  delivery_zones where code ='Centro'),(select id from  facilities where code ='F11A')),
((select id from  delivery_zones where code ='Centro'),(select id from  facilities where code ='F11B')),
((select id from  delivery_zones where code ='Centro'),(select id from  facilities where code ='F11C')),
((select id from  delivery_zones where code ='Centro'),(select id from  facilities where code ='F12A')),
((select id from  delivery_zones where code ='Centro'),(select id from  facilities where code ='F12B')),
((select id from  delivery_zones where code ='Centro'),(select id from  facilities where code ='F12C')),
((select id from  delivery_zones where code ='Centro'),(select id from  facilities where code ='F13A')),
((select id from  delivery_zones where code ='Centro'),(select id from  facilities where code ='F13B')),
((select id from  delivery_zones where code ='Centro'),(select id from  facilities where code ='F13C')),

((select id from  delivery_zones where code ='Sul'),(select id from  facilities where code ='F14A')),
((select id from  delivery_zones where code ='Sul'),(select id from  facilities where code ='F14B')),
((select id from  delivery_zones where code ='Sul'),(select id from  facilities where code ='F14C')),
((select id from  delivery_zones where code ='Sul'),(select id from  facilities where code ='F15A')),
((select id from  delivery_zones where code ='Sul'),(select id from  facilities where code ='F15B')),
((select id from  delivery_zones where code ='Sul'),(select id from  facilities where code ='F15C')),
((select id from  delivery_zones where code ='Sul'),(select id from  facilities where code ='F16A')),
((select id from  delivery_zones where code ='Sul'),(select id from  facilities where code ='F16B')),
((select id from  delivery_zones where code ='Sul'),(select id from  facilities where code ='F16C'));

INSERT INTO delivery_zone_warehouses ( deliveryZoneId ,warehouseId )values
((select id from  delivery_zones where code ='Norte'),(select id from  facilities where code ='W15')),
((select id from  delivery_zones where code ='Centro'),(select id from  facilities where code ='W16')),
((select id from  delivery_zones where code ='Sul'),(select id from  facilities where code ='W17')),
((select id from  delivery_zones where code ='Sul'),(select id from  facilities where code ='W18'));

INSERT INTO delivery_zone_program_schedules
(deliveryZoneId, programId, scheduleId ) values
((select id from delivery_zones where code='DZ1'),(select id from programs where code='VACCINES'),(select id from processing_schedules where code='M')),
((select id from delivery_zones where code='Norte'),(select id from programs where code='VACCINES'),(select id from processing_schedules where code='M')),
((select id from delivery_zones where code='Centro'),(select id from programs where code='VACCINES'),(select id from processing_schedules where code='M')),
((select id from delivery_zones where code='Sul'),(select id from programs where code='VACCINES'),(select id from processing_schedules where code='M'));
