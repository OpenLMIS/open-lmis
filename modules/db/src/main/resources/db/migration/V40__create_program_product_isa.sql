-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS program_product_isa;
CREATE TABLE program_product_isa (
    id SERIAL PRIMARY KEY,
    whoRatio NUMERIC(6,3) NOT NULL,
    dosesPerYear INTEGER NOT NULL,
    wastageRate NUMERIC(6,3) NOT NULL,
    programProductId INTEGER REFERENCES program_products(id) NOT NULL,
    bufferPercentage NUMERIC(6,3) NOT NULL,
    minimumValue INTEGER,
    maximumValue INTEGER,
    adjustmentValue INTEGER NOT NULL,
    calculatedIsa INTEGER,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX i_program_product_isa_programProductId ON program_product_isa(programProductId);





