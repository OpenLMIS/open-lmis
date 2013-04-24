-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

delete from programs;
insert into programs(code, name, description, active, templateConfigured, budgetingApplies, usesDar)
    values
    ('HIV', 'HIV', 'HIV', TRUE, FALSE, FALSE, FALSE),
    ('ESS_MEDS', 'ESSENTIAL MEDICINES', 'ESSENTIAL MEDICINES', TRUE, FALSE, TRUE, FALSE),
    ('TB', 'TB', 'TB', TRUE, FALSE, FALSE, FALSE),
    ('MALARIA', 'MALARIA', 'MALARIA', TRUE, FALSE, FALSE, FALSE);