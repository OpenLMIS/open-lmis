--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

DROP TABLE IF EXISTS program_product_price_history;
CREATE TABLE program_product_price_history (
    id SERIAL PRIMARY KEY,
    programProductId INTEGER REFERENCES program_products(id) NOT NULL,
    price NUMERIC(20,2) DEFAULT 0,
    pricePerDosage NUMERIC(20,2) DEFAULT 0,
    source VARCHAR(50),
    startDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    endDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    createdBy INTEGER,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX i_program_product_price_history_programProductId ON program_product_price_history(programProductId);
