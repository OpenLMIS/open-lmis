--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

CREATE TABLE requisition_line_items (
  id                          SERIAL PRIMARY KEY,
  rnrId                       INT         NOT NULL REFERENCES requisitions (id),
  productCode                 VARCHAR(50) NOT NULL REFERENCES products (code),
  product                     VARCHAR(250),
  productDisplayOrder         INTEGER,
  productCategory             VARCHAR(100),
  productCategoryDisplayOrder INTEGER,
  dispensingUnit              VARCHAR(20) NOT NULL,
  beginningBalance            INTEGER,
  quantityReceived            INTEGER,
  quantityDispensed           INTEGER,
  stockInHand                 INTEGER,
  quantityRequested           INTEGER,
  reasonForRequestedQuantity  TEXT,
  calculatedOrderQuantity     INTEGER,
  quantityApproved            INTEGER,
  totalLossesAndAdjustments   INTEGER,
  newPatientCount             INTEGER,
  stockOutDays                INTEGER,
  normalizedConsumption       INTEGER,
  amc                         INTEGER,
  maxMonthsOfStock            INTEGER     NOT NULL,
  maxStockQuantity            INTEGER,
  packsToShip                 INTEGER,
  price                       NUMERIC(15, 4),
  expirationDate              VARCHAR(10),
  remarks                     TEXT,
  dosesPerMonth               INTEGER     NOT NULL,
  dosesPerDispensingUnit      INTEGER     NOT NULL,
  packSize                    SMALLINT    NOT NULL,
  roundToZero                 BOOLEAN,
  packRoundingThreshold       INTEGER,
  fullSupply                  BOOLEAN     NOT NULL,
  skipped                     BOOLEAN     NOT NULL DEFAULT FALSE,
  reportingDays               INTEGER,
  createdBy                   INTEGER,
  createdDate                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                  INTEGER,
  modifiedDate                TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX i_requisition_line_items_rnrId_fullSupply_t ON requisition_line_items (rnrId)
  WHERE fullSupply = TRUE;
CREATE INDEX i_requisition_line_items_rnrId_fullSupply_f ON requisition_line_items (rnrId)
  WHERE fullSupply = FALSE;