--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

CREATE TABLE atomfeed.event_records (
  id SERIAL PRIMARY KEY,
  uuid VARCHAR(40),
  title VARCHAR(255),
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  uri VARCHAR(255),
  object VARCHAR(5000),
  category VARCHAR(255)
);


CREATE TABLE atomfeed.chunking_history (
  id SERIAL PRIMARY KEY,
  chunk_length BIGINT,
  start  BIGINT NOT NULL
);


INSERT INTO atomfeed.chunking_history(chunk_length, start) VALUES(5,1);
