--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

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






