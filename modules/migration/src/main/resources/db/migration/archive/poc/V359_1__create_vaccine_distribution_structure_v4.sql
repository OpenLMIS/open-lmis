
DROP TABLE IF EXISTS vaccine_distribution_line_items;

DROP TABLE IF EXISTS vaccine_distribution_batches;


-- Table: distribution_types
DROP TABLE IF EXISTS distribution_types;
CREATE TABLE distribution_types
(
  id                SERIAL PRIMARY KEY                            ,
  name              VARCHAR (100) UNIQUE                        NOT NULL,
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
(1  , 'SUPPLY'),
(2  , 'RECALL');
 




-- Table: vaccine_distribution_batches
CREATE TABLE  vaccine_distribution_batches
(
  id SERIAL PRIMARY KEY, 
  batchId integer,
  dispatchId character varying(100),
  expiryDate timestamp without time zone,
  productionDate timestamp without time zone,
  manufacturerId integer REFERENCES manufacturers (id) NOT NULL,
  donorId integer REFERENCES donors (id) NOT NULL,
  receiveDate date,
  recallDate date,
  productCode character varying(50) REFERENCES products (code) NOT NULL,
  voucherNumber integer,
  originId integer,
  fromFacilityId integer REFERENCES facilities (id) NOT NULL,
  toFacilityId integer REFERENCES facilities (id) NOT NULL,
  distributionTypeId character varying(100) REFERENCES distribution_types(name),
  vialsPerBox integer,
  boxLength integer,
  boxWidth integer,
  boxHeight integer,
  unitCost integer,
  totalCost integer,
  purposeId integer,
  freight integer,
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


-- Table: vaccine_distribution_line_items
CREATE TABLE vaccine_distribution_line_items
(
  id SERIAL PRIMARY KEY, 
  distributionBatchId integer REFERENCES vaccine_distribution_batches (id) NOT NULL,
  quantityReceived double precision,
  vvmStage integer,
  confirmed boolean,
  comments VARCHAR(250),
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

