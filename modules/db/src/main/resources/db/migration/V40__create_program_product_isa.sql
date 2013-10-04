--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

DROP TABLE IF EXISTS program_product_isa;
CREATE TABLE program_product_isa (
  id               SERIAL PRIMARY KEY,
  whoRatio         NUMERIC(6, 3)                            NOT NULL,
  dosesPerYear     INTEGER                                  NOT NULL,
  wastageFactor    NUMERIC(6, 3)                            NOT NULL,
  programProductId INTEGER REFERENCES program_products (id) NOT NULL,
  bufferPercentage NUMERIC(6, 3)                            NOT NULL,
  minimumValue     INTEGER,
  maximumValue     INTEGER,
  adjustmentValue  INTEGER                                  NOT NULL,
  createdBy        INTEGER,
  createdDate      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy       INTEGER,
  modifiedDate     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX i_program_product_isa_programProductId ON program_product_isa (programProductId);





