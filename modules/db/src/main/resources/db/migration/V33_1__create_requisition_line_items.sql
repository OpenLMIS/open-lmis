-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS requisition_line_items;
CREATE TABLE requisition_line_items (
id                                    SERIAL PRIMARY KEY,
rnrId                                 INT NOT NULL REFERENCES requisitions(id),
productCode                           VARCHAR(50) NOT NULL REFERENCES products(code),
product                               VARCHAR(250) ,
productDisplayOrder                   INTEGER,
productCategory                       VARCHAR (100),
productCategoryDisplayOrder           INTEGER,
dispensingUnit                        VARCHAR(20) not null,
beginningBalance                      INTEGER,
quantityReceived                      INTEGER,
quantityDispensed                     INTEGER,
stockInHand                           INTEGER,
quantityRequested                     INTEGER,
reasonForRequestedQuantity            TEXT,
calculatedOrderQuantity               INTEGER,
quantityApproved                      INTEGER,
totalLossesAndAdjustments             INTEGER,
newPatientCount                       INTEGER,
stockOutDays                          INTEGER,
normalizedConsumption                 INTEGER,
amc                                   INTEGER,
maxMonthsOfStock                      INTEGER NOT NULL,
maxStockQuantity                      INTEGER,
packsToShip                           INTEGER,
price                                 NUMERIC(15, 4),
remarks                               TEXT,
dosesPerMonth                         INTEGER NOT NULL,
dosesPerDispensingUnit                INTEGER NOT NULL,
packSize                              SMALLINT NOT NULL,
roundToZero                           BOOLEAN,
packRoundingThreshold                 INTEGER,
fullSupply                            BOOLEAN NOT NULL,
previousStockInHandAvailable          BOOLEAN NOT NULL DEFAULT FALSE,
modifiedBy                            INTEGER,
modifiedDate                          TIMESTAMP  DEFAULT  NOW(),
createdBy                             INTEGER,
createdDate                           TIMESTAMP DEFAULT NOW()
);

CREATE INDEX i_requisition_line_items_rnrId_fullSupply_t ON requisition_line_items(rnrId) WHERE fullSupply = TRUE;
CREATE INDEX i_requisition_line_items_rnrId_fullSupply_f ON requisition_line_items(rnrId) WHERE fullSupply = FALSE;