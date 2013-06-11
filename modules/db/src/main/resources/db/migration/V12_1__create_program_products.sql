-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS program_products;
CREATE TABLE program_products (
    id SERIAL PRIMARY KEY,
    programId INTEGER REFERENCES programs(id) NOT NULL,
    productId INTEGER REFERENCES products(id) NOT NULL,
    programProductISAId INTEGER REFERENCES program_product_isa(id),
    dosesPerMonth INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    currentPrice NUMERIC(20,2) DEFAULT 0,
    createdBy INTEGER,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (productId, programId)
);

CREATE INDEX i_program_product_programId_productId ON program_products(programId, productId);
CREATE INDEX i_program_product_programProductISAId ON program_products(programProductISAId);