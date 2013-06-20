-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DELETE FROM regimen_categories;

INSERT INTO regimen_categories (code, name, displayOrder) VALUES
('ADULTS', 'Adults', 1),
('PAEDIATRICS', 'Paediatrics', 2);