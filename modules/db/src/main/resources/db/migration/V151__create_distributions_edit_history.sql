--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

CREATE TABLE distributions_edit_history (
  distributionId    INTEGER NOT NULL REFERENCES distributions (id),
  district          VARCHAR(500) NOT NULL,
  facilityId        INTEGER NOT NULL REFERENCES facilities (id),
  dataScreen        VARCHAR(500) NOT NULL,
  editedItem        VARCHAR(500) NOT NULL,
  originalValue     VARCHAR(500) NOT NULL,
  newValue          VARCHAR(500) NOT NULL,
  editedDatetime    TIMESTAMP NOT NULL,
  editedBy          INTEGER NOT NULL REFERENCES users (id)
);
