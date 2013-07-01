-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS facility_program_products;
CREATE TABLE facility_program_products (
    id SERIAL PRIMARY KEY,
    facilityId INTEGER NOT NULL REFERENCES facilities(id),
    programProductId INTEGER NOT NULL REFERENCES program_products(id),
    overriddenISA INTEGER,
    createdBy INTEGER,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (facilityId, programProductId)
);

CREATE UNIQUE INDEX uc_facility_program_products_overriddenIsa_programProductId
ON facility_program_products(facilityId, programProductId);






