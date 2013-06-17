-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS program_product_isa;
CREATE TABLE program_product_isa (
    id SERIAL PRIMARY KEY,
    whoRatio INTEGER NOT NULL,
    dosesPerYear INTEGER NOT NULL,
    wastageRate INTEGER NOT NULL,
    programProductId INTEGER REFERENCES program_products(id),
    bufferPercentage INTEGER NOT NULL,
    minimumValue INTEGER,
    adjustmentValue INTEGER NOT NULL,
    calculatedIsa INTEGER,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX i_program_product_isa_programProductId ON program_product_isa(programProductId);





