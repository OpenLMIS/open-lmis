--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

DROP TABLE IF EXISTS products;
CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE ,
  alternateItemCode VARCHAR(20),
  manufacturer VARCHAR(100),
  manufacturerCode VARCHAR(30),
  manufacturerBarcode VARCHAR(20),
  mohBarcode VARCHAR(20),
  gtin VARCHAR(20),
  type VARCHAR(100),
  displayOrder INTEGER,
  primaryName VARCHAR(150) NOT NULL,
  fullName VARCHAR(250),
  genericName VARCHAR(100),
  alternateName VARCHAR(100),
  description VARCHAR(250),
  strength VARCHAR(14),
  formID INTEGER REFERENCES product_forms(id),
  dosageUnitId INTEGER REFERENCES dosage_units(id),
  categoryId INTEGER REFERENCES product_categories(id),
  productGroupId INTEGER REFERENCES product_groups(id),
  dispensingUnit VARCHAR(20) NOT NULL,
  dosesPerDispensingUnit SMALLINT NOT NULL,
  packSize SMALLINT NOT NULL,
  alternatePackSize SMALLINT,
  storeRefrigerated  BOOLEAN,
  storeRoomTemperature BOOLEAN,
  hazardous BOOLEAN,
  flammable BOOLEAN,
  controlledSubstance BOOLEAN,
  lightSensitive BOOLEAN,
  approvedByWho BOOLEAN,
  contraceptiveCyp NUMERIC(8,4),
  packLength NUMERIC(8,4),
  packWidth NUMERIC(8,4),
  packHeight NUMERIC(8,4),
  packWeight NUMERIC(8,4),
  packsPerCarton SMALLINT,
  cartonLength NUMERIC(8,4),
  cartonWidth NUMERIC(8,4),
  cartonHeight NUMERIC(8,4),
  cartonsPerPallet SMALLINT,
  expectedShelfLife SMALLINT,
  specialStorageInstructions TEXT,
  specialTransportInstructions TEXT,
  active BOOLEAN NOT NULL,
  fullSupply BOOLEAN NOT NULL,
  tracer BOOLEAN NOT NULL,
  roundToZero BOOLEAN NOT NULL,
  archived BOOLEAN,
  packRoundingThreshold SMALLINT NOT NULL,
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uc_products_lower_code ON products(LOWER(code));