-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

delete from programs;
insert into programs(code, name, description, budgetingApplies, usesDar, active, templateConfigured)
    values
    ('HIV','HIV','HIV',FALSE,FALSE,TRUE, FALSE),
    ('ESS_MEDS','ESSENTIAL MEDICINES','ESSENTIAL MEDICINES',TRUE,FALSE,TRUE, FALSE),
    ('TB','TB','TB',FALSE,FALSE,TRUE, FALSE),
    ('MALARIA','MALARIA','MALARIA',FALSE,FALSE,TRUE, FALSE);