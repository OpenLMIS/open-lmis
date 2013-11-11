--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

CREATE TABLE facilities (
  id                       SERIAL PRIMARY KEY,
  code                     VARCHAR(50) UNIQUE NOT NULL,
  name                     VARCHAR(50)        NOT NULL,
  description              VARCHAR(250),
  gln                      VARCHAR(30),
  mainPhone                VARCHAR(20),
  fax                      VARCHAR(20),
  address1                 VARCHAR(50),
  address2                 VARCHAR(50),
  geographicZoneId         INTEGER            NOT NULL REFERENCES geographic_zones (id),
  typeId                   INTEGER            NOT NULL REFERENCES facility_types (id),
  catchmentPopulation      INTEGER,
  latitude                 NUMERIC(8, 5),
  longitude                NUMERIC(8, 5),
  altitude                 NUMERIC(8, 4),
  operatedById             INTEGER REFERENCES facility_operators (id),
  coldStorageGrossCapacity NUMERIC(8, 4),
  coldStorageNetCapacity   NUMERIC(8, 4),
  suppliesOthers           BOOLEAN,
  sdp                      BOOLEAN            NOT NULL,
  online                   BOOLEAN,
  satellite                BOOLEAN,
  parentFacilityId         INTEGER REFERENCES facilities (id),
  hasElectricity           BOOLEAN,
  hasElectronicSCC         BOOLEAN,
  hasElectronicDAR         BOOLEAN,
  active                   BOOLEAN            NOT NULL,
  goLiveDate               DATE               NOT NULL,
  goDownDate               DATE,
  comment                  TEXT,
  enabled                  BOOLEAN            NOT NULL,
  virtualFacility          BOOLEAN            NOT NULL,
  createdBy                INTEGER,
  createdDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy               INTEGER,
  modifiedDate             TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX i_facility_name ON facilities (name);

CREATE UNIQUE INDEX uc_facilities_lower_code ON facilities (LOWER(code));