-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DELETE FROM programs;
INSERT INTO programs(code, name, description, active, templateConfigured, regimenTemplateConfigured, budgetingApplies, usesDar, push)
    VALUES
    ('HIV', 'HIV', 'HIV', TRUE, FALSE, FALSE, FALSE, FALSE, FALSE),
    ('ESS_MEDS', 'ESSENTIAL MEDICINES', 'ESSENTIAL MEDICINES', TRUE, FALSE,FALSE, TRUE, FALSE, FALSE),
    ('TB', 'TB', 'TB', TRUE, FALSE,FALSE, FALSE, FALSE, FALSE),
    ('MALARIA', 'MALARIA', 'MALARIA', TRUE, FALSE, FALSE, FALSE, FALSE, FALSE),
    ('VACCINES', 'VACCINES', 'VACCINES', TRUE, FALSE,FALSE, FALSE, FALSE, TRUE);