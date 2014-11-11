-------------------------------------------------
-- Table: vaccine_distribution_types
DROP TABLE IF EXISTS vaccine_distribution_types;
CREATE TABLE vaccine_distribution_types
(
  id integer NOT NULL,
  colde character varying(50),
  name character varying(250),
  nature character varying(2),
  CONSTRAINT vaccine_distribution_types_pkey PRIMARY KEY (id)
);
ALTER TABLE vaccine_distribution_types OWNER TO postgres;  
--------------------------------------------------------
-- Table: vaccine_targets
DROP TABLE IF EXISTS vaccine_targets;
CREATE TABLE vaccine_targets
(
  id                             SERIAL PRIMARY KEY                               ,
  geographicZoneId               INTEGER REFERENCES geographic_zones (id) NOT NULL,
  targetYear                     INTEGER                                  NOT NULL,
  population                     INTEGER                                  NOT NULL,
  expectedBirths                 INTEGER                                          ,
  expectedPregnancies            INTEGER                                          ,
  servingInfants                 INTEGER                                          ,
  survivingInfants               INTEGER                                          ,
  children1Yr                    INTEGER                                          ,
  children2Yr                    INTEGER                                          ,
  girls9_13Yr                    INTEGER                                          ,
  createdBy                      INTEGER                                          ,
  createdDate                    TIMESTAMP            DEFAULT CURRENT_TIMESTAMP   ,
  modifiedBy                     INTEGER                                          ,
  modifiedDate                   TIMESTAMP            DEFAULT CURRENT_TIMESTAMP   
);

CREATE UNIQUE INDEX uc_vaccine_targets_year ON vaccine_targets(geographicZoneId,targetYear);

COMMENT ON TABLE vaccine_targets IS                      'Demographics and targets for the vaccine program'; 
COMMENT ON INDEX uc_vaccine_targets_year IS              'One target per geographic zone allowed'; 

COMMENT ON COLUMN vaccine_targets.id IS                  'ID';
COMMENT ON COLUMN vaccine_targets.geographicZoneId IS    'Zone ID';
COMMENT ON COLUMN vaccine_targets.targetYear IS          'Year';
COMMENT ON COLUMN vaccine_targets.population IS          'Population';
COMMENT ON COLUMN vaccine_targets.expectedBirths IS      'Expected births';
COMMENT ON COLUMN vaccine_targets.expectedPregnancies IS 'Expected pregnancies';
COMMENT ON COLUMN vaccine_targets.servingInfants IS      'Serving infants';
COMMENT ON COLUMN vaccine_targets.survivingInfants IS    'Surviving infants';
COMMENT ON COLUMN vaccine_targets.children1Yr IS         'Children Below 1 year';
COMMENT ON COLUMN vaccine_targets.children2Yr IS         'Children Below 2 year';
COMMENT ON COLUMN vaccine_targets.girls9_13Yr IS         'Girls between 9 to 13 years';
COMMENT ON COLUMN vaccine_targets.createdBy IS           'Created by';
COMMENT ON COLUMN vaccine_targets.createdDate IS         'Created on';
COMMENT ON COLUMN vaccine_targets.modifiedBy IS          'Modified by';
COMMENT ON COLUMN vaccine_targets.modifiedDate IS        'Modified on';
 
-------------------------------------------------------------------
-- Table: vaccine_quantifications
DROP TABLE IF EXISTS vaccine_quantifications;
CREATE TABLE vaccine_quantifications
(
  id                         SERIAL PRIMARY KEY                             ,
  programId                  INTEGER REFERENCES programs (id)       NOT NULL,
  quantificationYear         INTEGER                                NOT NULL,
  vaccineTypeId              INTEGER                                NOT NULL,
  productCode                VARCHAR(50) REFERENCES products (code) NOT NULL,
  targetPopulation           INTEGER                                NOT NULL,
  targetPopulationPercent    INTEGER                                NOT NULL,
  dosesPerTarget             NUMERIC(8,4)                                   ,
  presentation               INTEGER                                        ,
  expectedCoverage           INTEGER                                        ,
  wastageRate                INTEGER                                        ,
  administrationModeId       INTEGER                                        ,
  dilutionId                 INTEGER                                        ,
  supplyInterval             NUMERIC(4,2)                                   ,
  safetyStock                NUMERIC(4,2)                                   ,
  leadTime                   NUMERIC(4,2)                                   ,
  createdBy                  INTEGER                                        ,
  createdDate                TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                 INTEGER                                        ,
  modifiedDate               TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uc_vaccine_quantifications_year ON vaccine_quantifications(programId,quantificationYear,vaccineTypeId,productCode);

COMMENT ON TABLE vaccine_quantifications IS                       'Parameters to be used for vaccine quantifications';
COMMENT ON INDEX uc_vaccine_quantifications_year IS               'One vaccine quantification parameter per year allowed';

COMMENT ON COLUMN vaccine_quantifications.id IS                      'ID';
COMMENT ON COLUMN vaccine_quantifications.programId IS               'Program';
COMMENT ON COLUMN vaccine_quantifications.quantificationYear IS      'Year';
COMMENT ON COLUMN vaccine_quantifications.vaccineTypeId IS           'Vaccine type';
COMMENT ON COLUMN vaccine_quantifications.productCode IS             'Product';
COMMENT ON COLUMN vaccine_quantifications.targetPopulation IS        'Target population';
COMMENT ON COLUMN vaccine_quantifications.targetPopulationPercent IS 'Target population percentage';
COMMENT ON COLUMN vaccine_quantifications.dosesPerTarget IS          'Doses per target';
COMMENT ON COLUMN vaccine_quantifications.presentation IS            'Presentation';
COMMENT ON COLUMN vaccine_quantifications.expectedCoverage IS        'Expected coverage';
COMMENT ON COLUMN vaccine_quantifications.wastageRate IS             'Wastage rate';
COMMENT ON COLUMN vaccine_quantifications.administrationModeId IS    'Administration mode';
COMMENT ON COLUMN vaccine_quantifications.dilutionId IS              'Diluation';
COMMENT ON COLUMN vaccine_quantifications.supplyInterval IS          'Supply interval (months)';
COMMENT ON COLUMN vaccine_quantifications.safetyStock IS             'Safety stock';
COMMENT ON COLUMN vaccine_quantifications.leadTime IS                'Lead time';
COMMENT ON COLUMN vaccine_quantifications.createdBy IS               'Created by';
COMMENT ON COLUMN vaccine_quantifications.createdDate IS             'Created on';
COMMENT ON COLUMN vaccine_quantifications.modifiedBy IS              'Modified by';
COMMENT ON COLUMN vaccine_quantifications.modifiedDate IS            'Modified on';

-------------------------------------------------------------------
-- Table: storage_types
DROP TABLE IF EXISTS vaccine_storage;
DROP TABLE IF EXISTS storage_types;
CREATE TABLE storage_types
(
  id                SERIAL PRIMARY KEY                            ,
  storageTypeName   VARCHAR (100)                         NOT NULL,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uc_storage_types_lower_name ON storage_types(LOWER(storageTypeName));

COMMENT ON TABLE storage_types IS                   'Vaccine storage types';
COMMENT ON INDEX uc_storage_types_lower_name IS     'Unique storage type required';

COMMENT ON COLUMN storage_types.id IS               'ID';
COMMENT ON COLUMN storage_types.storageTypeName IS  'Storage type';
COMMENT ON COLUMN storage_types.createdBy IS        'Created by';
COMMENT ON COLUMN storage_types.createdDate IS      'Created on';
COMMENT ON COLUMN storage_types.modifiedBy IS       'Modified by';
COMMENT ON COLUMN storage_types.modifiedDate IS     'Modified on';

-- Seed with initial list
INSERT INTO storage_types
(id , storageTypeName)

VALUES
(1  , 'Walk-in cold room'),
(2  , 'Walk-in freezer room'),
(3  , 'Dry storage');

-------------------------------------------------------------------
-- Table: temperature
DROP TABLE IF EXISTS temperature;
CREATE TABLE temperature
(
  id                SERIAL PRIMARY KEY                            ,
  temperatureName   VARCHAR (100)                         NOT NULL,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uc_temperature_lower_name ON temperature(LOWER(temperatureName));

COMMENT ON TABLE temperature IS                     'Vaccine storage temperature';
COMMENT ON INDEX uc_temperature_lower_name IS       'Unique temperature required';

COMMENT ON COLUMN temperature.id IS                 'ID';
COMMENT ON COLUMN temperature.temperatureName IS    'Temperature';
COMMENT ON COLUMN temperature.createdBy IS          'Created by';
COMMENT ON COLUMN temperature.createdDate IS        'Created on';
COMMENT ON COLUMN temperature.modifiedBy IS         'Modified by';
COMMENT ON COLUMN temperature.modifiedDate IS       'Modified on';

-- Seed with initial list
INSERT INTO temperature
(id , temperatureName)

VALUES
(1  , 'Ambient'),
(2  , '+5° C'),
(3  , '-20° C');

-------------------------------------------------------------------
-- Table: vaccine_storage

CREATE TABLE vaccine_storage
(
  id                SERIAL PRIMARY KEY                            ,
  storageTypeId     INTEGER REFERENCES storage_types (id) NOT NULL,
  location          VARCHAR (100)                                 ,
  grossCapacity     INTEGER                                       ,
  netCapacity       INTEGER                                       ,
  temperatureId     INTEGER REFERENCES temperature (id)   NOT NULL,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP  
);
CREATE UNIQUE INDEX uc_vaccine_storage_code ON vaccine_storage(location);

COMMENT ON TABLE vaccine_storage IS                    'Vaccine storage capacity';
COMMENT ON INDEX uc_vaccine_storage_code IS            'Unique code required for storage location';

COMMENT ON COLUMN vaccine_storage.id IS                'ID';
COMMENT ON COLUMN vaccine_storage.storageTypeId IS     'Storage type';
COMMENT ON COLUMN vaccine_storage.location IS          'Storage location code';
COMMENT ON COLUMN vaccine_storage.grossCapacity IS     'Grosss capacity (liters)';
COMMENT ON COLUMN vaccine_storage.netCapacity IS       'Net capacity (liters)';
COMMENT ON COLUMN vaccine_storage.temperatureId IS     'Temperature';
COMMENT ON COLUMN vaccine_storage.createdBy IS         'Created by';
COMMENT ON COLUMN vaccine_storage.createdDate IS       'Created on';
COMMENT ON COLUMN vaccine_storage.modifiedBy IS        'Modified by';
COMMENT ON COLUMN vaccine_storage.modifiedDate IS      'Modified on';

-----------------------------------------
-- Table: Manufacturers
DROP TABLE IF EXISTS vaccine_distribution_line_items;
DROP TABLE IF EXISTS vaccine_distribution_batches;
DROP TABLE IF EXISTS manufacturers;

CREATE TABLE manufacturers
(
  id serial PRIMARY KEY,
  name character varying(1000) NOT NULL,
  website character varying(1000) NOT NULL,
  contactPerson character varying(200),
  primaryPhone character varying(20),
  email character varying(200),
  description character varying(2000),
  specialization character varying(2000),
  geographicCoverage character varying(2000),
  registrationDate date,
  createdBy integer,
  createdDate timestamp without time zone DEFAULT now(),
  modifiedBy integer,
  modifiedDate timestamp without time zone DEFAULT now()
);
ALTER TABLE manufacturers OWNER TO postgres;
CREATE UNIQUE INDEX uc_manufacturers_lower_name ON manufacturers(LOWER(name));

COMMENT ON TABLE manufacturers IS 'Manufacturers';
COMMENT ON COLUMN manufacturers.id IS 'id';
COMMENT ON COLUMN manufacturers.name  IS 'name';
COMMENT ON COLUMN manufacturers.website IS 'website'; 
COMMENT ON COLUMN manufacturers.contactPerson IS 'contactPerson';
COMMENT ON COLUMN manufacturers.primaryPhone IS 'primaryPhone';
COMMENT ON COLUMN manufacturers.email IS 'email';
COMMENT ON COLUMN manufacturers.description IS 'description';
COMMENT ON COLUMN manufacturers.specialization IS 'specialization';
COMMENT ON COLUMN manufacturers.geographicCoverage IS 'geographicCoverage';
COMMENT ON COLUMN manufacturers.registrationDate IS 'registrationDate';
COMMENT ON COLUMN manufacturers.createdBy IS 'createdBy';
COMMENT ON COLUMN manufacturers.createdDate IS 'createdDate';
COMMENT ON COLUMN manufacturers.modifiedBy IS 'modifiedBy';
COMMENT ON COLUMN manufacturers.modifiedDate IS 'modifiedDate';

------------------------------------------------------------------------
-- Table: distribution_types
DROP TABLE IF EXISTS distribution_types;
CREATE TABLE distribution_types
(
  id                SERIAL PRIMARY KEY                            ,
  name              VARCHAR (100)                         NOT NULL,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uc_distribution_types_lower_name ON distribution_types(LOWER(name));

COMMENT ON TABLE distribution_types IS                   'Vaccine storage types';
COMMENT ON INDEX uc_distribution_types_lower_name IS     'Unique storage type required';

COMMENT ON COLUMN distribution_types.id IS               'ID';
COMMENT ON COLUMN distribution_types.name IS  'Distribution type';
COMMENT ON COLUMN distribution_types.createdBy IS        'Created by';
COMMENT ON COLUMN distribution_types.createdDate IS      'Created on';
COMMENT ON COLUMN distribution_types.modifiedBy IS       'Modified by';
COMMENT ON COLUMN distribution_types.modifiedDate IS     'Modified on';

-- Seed with initial list
INSERT INTO distribution_types
(id , name)

VALUES
(1  , 'Supply'),
(2  , 'Recall');

-----------------------------------------------
-- Table: vaccine_distribution_batches
CREATE TABLE  vaccine_distribution_batches
(
  id SERIAL PRIMARY KEY, 
  batchId integer,
  expiryDate timestamp without time zone,
  productionDate timestamp without time zone,
  manufacturerId integer REFERENCES manufacturers (id) NOT NULL,
  donorId integer REFERENCES donors (id) NOT NULL,
  receiveDate date,
  productCode character varying(50) REFERENCES products (code) NOT NULL,
  fromFacilityId integer REFERENCES facilities (id) NOT NULL,
  toFacilityId integer REFERENCES facilities (id) NOT NULL,
  distributionTypeId integer,
  vialsPerBox integer,
  boxLength integer,
  boxWidth integer,
  boxHeight integer,
  unitCost integer,
  totalCost integer,
  purposeId integer,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP  
);
ALTER TABLE vaccine_distribution_batches OWNER TO postgres;
COMMENT ON TABLE vaccine_distribution_batches IS  'vaccine distribution batches';
COMMENT ON COLUMN vaccine_distribution_batches.id IS  'id';
COMMENT ON COLUMN vaccine_distribution_batches.batchId IS  'batchId';
COMMENT ON COLUMN vaccine_distribution_batches.expiryDate IS  'expiryDate';
COMMENT ON COLUMN vaccine_distribution_batches.productionDate IS  'productionDate';
COMMENT ON COLUMN vaccine_distribution_batches.manufacturerId IS  'manufacturerId';
COMMENT ON COLUMN vaccine_distribution_batches.donorId IS  'donorId';
COMMENT ON COLUMN vaccine_distribution_batches.receiveDate IS  'receiveDate';
COMMENT ON COLUMN vaccine_distribution_batches.productCode IS  'productCode';
COMMENT ON COLUMN vaccine_distribution_batches.fromFacilityId IS  'fromFacilityId';
COMMENT ON COLUMN vaccine_distribution_batches.toFacilityId IS  'toFacilityId';
COMMENT ON COLUMN vaccine_distribution_batches.distributionTypeId IS  'distributionType';
COMMENT ON COLUMN vaccine_distribution_batches.vialsPerBox IS  'vialsPerBox';
COMMENT ON COLUMN vaccine_distribution_batches.boxLength IS  'boxLength';
COMMENT ON COLUMN vaccine_distribution_batches.boxWidth IS  'boxWidth';
COMMENT ON COLUMN vaccine_distribution_batches.boxHeight IS  'boxHeight';
COMMENT ON COLUMN vaccine_distribution_batches.unitCost IS  'unitCost';
COMMENT ON COLUMN vaccine_distribution_batches.totalCost IS  'totalCost';
COMMENT ON COLUMN vaccine_distribution_batches.purposeId IS  'purposeId';
COMMENT ON COLUMN vaccine_distribution_batches.createdBy IS  'createdBy';
COMMENT ON COLUMN vaccine_distribution_batches.createdDate IS  'createdDate';
COMMENT ON COLUMN vaccine_distribution_batches.modifiedBy IS  'modifiedBy';
COMMENT ON COLUMN vaccine_distribution_batches.modifiedDate IS  'modifiedDate';

--------------------------------------------
-- Table: vaccine_distribution_line_items
CREATE TABLE vaccine_distribution_line_items
(
  id SERIAL PRIMARY KEY, 
  distributionBatchId integer REFERENCES vaccine_distribution_batches (id) NOT NULL,
  quantityReceived double precision,
  vvmStage integer,
  confirmed integer,
  comments integer,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP  

);
ALTER TABLE vaccine_distribution_line_items OWNER TO postgres;
COMMENT ON TABLE vaccine_distribution_line_items IS 'vaccine distribution line items';
COMMENT ON COLUMN vaccine_distribution_line_items.id  IS 'id';
COMMENT ON COLUMN vaccine_distribution_line_items.distributionBatchId IS 'distributionBatchId';
COMMENT ON COLUMN vaccine_distribution_line_items.quantityReceived IS 'quantityReceived';
COMMENT ON COLUMN vaccine_distribution_line_items.vvmStage IS 'vvmStage';
COMMENT ON COLUMN vaccine_distribution_line_items.confirmed IS 'confirmed';
COMMENT ON COLUMN vaccine_distribution_line_items.comments IS 'comments';  