--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

DROP TABLE IF EXISTS facility_approved_products;
CREATE TABLE facility_approved_products (
    id SERIAL PRIMARY KEY,
    facilityTypeId INTEGER REFERENCES facility_types(id) NOT NULL,
    programProductId INTEGER REFERENCES program_products(id) NOT NULL,
    maxMonthsOfStock INTEGER NOT NULL,
    minMonthsOfStock NUMERIC(4,2) ,
    eop NUMERIC(4,2),
    createdBy INTEGER,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (facilityTypeId, programProductId)
);

CREATE INDEX i_facility_approved_product_programProductId_facilityTypeId ON facility_approved_products(programProductId, facilityTypeId);
