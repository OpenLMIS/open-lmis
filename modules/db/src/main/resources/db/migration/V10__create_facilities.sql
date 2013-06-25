-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE facilities (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(250),
    gln VARCHAR(30),
    mainPhone VARCHAR(20),
    fax VARCHAR(20),
    address1 VARCHAR(50),
    address2 VARCHAR(50),
    geographicZoneId INTEGER NOT NULL REFERENCES geographic_zones(id),
    typeId INTEGER NOT NULL REFERENCES facility_types(id),
    catchmentPopulation INTEGER,
    latitude NUMERIC(8,5),
    longitude NUMERIC(8,5),
    altitude NUMERIC(8,4),
    operatedById INTEGER REFERENCES facility_operators(id),
    coldStorageGrossCapacity NUMERIC(8,4),
    coldStorageNetCapacity NUMERIC(8,4),
    suppliesOthers BOOLEAN,
    sdp BOOLEAN NOT NULL,
    online BOOLEAN,
    satellite BOOLEAN,
    satelliteParentId INTEGER REFERENCES facilities(id),
    hasElectricity BOOLEAN,
    hasElectronicScc BOOLEAN,
    hasElectronicDar BOOLEAN,
    active BOOLEAN NOT NULL,
    goLiveDate DATE NOT NULL,
    goDownDate DATE,
    comment  TEXT,
    dataReportable BOOLEAN,
    createdBy INTEGER,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX i_facility_name ON facilities(name);

CREATE UNIQUE INDEX uc_facilities_lower_code ON facilities(LOWER(code));