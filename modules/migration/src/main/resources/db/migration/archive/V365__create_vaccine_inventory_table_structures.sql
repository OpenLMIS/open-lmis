DROP TABLE IF EXISTS on_hand;
DROP TABLE IF EXISTS inventory_batches;
DROP TABLE IF EXISTS inventory_transactions;

DROP TABLE IF EXISTS received_status;
-------------------------------------------------------------------
-- Table: transaction_types
DROP TABLE IF EXISTS transaction_types;
CREATE TABLE transaction_types
(
  id                SERIAL PRIMARY KEY                            ,
  name              VARCHAR (100)                         NOT NULL,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uc_transaction_types_lower_name ON transaction_types(LOWER(name));
COMMENT ON TABLE transaction_types IS                     'Inventory transaction types';
COMMENT ON INDEX uc_transaction_types_lower_name IS       'Unique transaction types required';
COMMENT ON COLUMN transaction_types.id IS                 'ID';
COMMENT ON COLUMN transaction_types.name IS               'Transaction Name';
COMMENT ON COLUMN transaction_types.createdBy IS          'Created by';
COMMENT ON COLUMN transaction_types.createdDate IS        'Created on';
COMMENT ON COLUMN transaction_types.modifiedBy IS         'Modified by';
COMMENT ON COLUMN transaction_types.modifiedDate IS       'Modified on';
-- Seed with initial list
INSERT INTO transaction_types
(id , name)
VALUES
(1  , 'Received'),
(2  , 'Issued'),
(3  , 'Adjustment');
-------------------------------------------------------------------

-- Table: countries
DROP TABLE IF EXISTS countries;
CREATE TABLE countries
(
  id                SERIAL PRIMARY KEY                            ,
  name              VARCHAR (100)                         NOT NULL,
  longName          VARCHAR (250)                                 ,
  isoCode2          VARCHAR (2)                                   ,
  isoCode3          VARCHAR (3)                                   ,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uc_countries_lower_name ON countries(LOWER(name));
COMMENT ON TABLE countries IS                           'Countries';
COMMENT ON INDEX uc_countries_lower_name IS             'Unique country name required';
COMMENT ON COLUMN countries.id IS                       'ID';
COMMENT ON COLUMN countries.name IS                     'Name';
COMMENT ON COLUMN countries.longName IS                 'Long name';
COMMENT ON COLUMN countries.isoCode2 IS                 'ISO code (2 digit)';
COMMENT ON COLUMN countries.isoCode3 IS                 'ISO code (3 digit)';
COMMENT ON COLUMN countries.createdBy IS                'Created by';
COMMENT ON COLUMN countries.createdDate IS              'Created on';
COMMENT ON COLUMN countries.modifiedBy IS               'Modified by';
COMMENT ON COLUMN countries.modifiedDate IS             'Modified on';
-------------------------------------------------------------------

-- Table: received_status
DROP TABLE IF EXISTS received_status;
CREATE TABLE received_status
(
  id                SERIAL PRIMARY KEY                            ,
  name              VARCHAR (100)                         NOT NULL,
  transactionTypeId INTEGER REFERENCES transaction_types (ID) NOT NULL,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uc_received_status_lower_name ON received_status(LOWER(name));
COMMENT ON TABLE received_status IS                     'Shipment received status';
COMMENT ON INDEX uc_received_status_lower_name IS       'Unique shipment received status required';
COMMENT ON COLUMN received_status.id IS                 'ID';
COMMENT ON COLUMN received_status.name IS               'Name';
COMMENT ON COLUMN received_status.createdBy IS          'Created by';
COMMENT ON COLUMN received_status.createdDate IS        'Created on';
COMMENT ON COLUMN received_status.modifiedBy IS         'Modified by';
COMMENT ON COLUMN received_status.modifiedDate IS       'Modified on';
-- Seed with initial list
INSERT INTO received_status
(name, transactionTypeId)
VALUES
('At Receiving area',1),
('Quarantined',1),
('QA inspected',1),
('Put to storage',1),
('At loading dock',2),
('Truck left',2),
('Distributed',2),
('Proof of receipt',2),
('Returned',2);

CREATE TABLE inventory_transactions
(
  id                    SERIAL PRIMARY KEY                                            ,
  transactionTypeId     INTEGER REFERENCES transaction_types             (id) NOT NULL,
  fromFacilityId        INTEGER REFERENCES facilities                    (id) NOT NULL,
  toFacilityId          INTEGER REFERENCES facilities                    (id) NOT NULL,
  productId             INTEGER REFERENCES products                      (id) NOT NULL,  
  dispatchReference     VARCHAR(200)                                          NOT NULL,
  dispatchDate          DATE                                      DEFAULT NOW(),
  bol                   VARCHAR(200)                                                  ,
  donorId               INTEGER REFERENCES donors                        (id)         ,
  originCountryId       INTEGER REFERENCES countries                     (id)         , 
  manufacturerId        INTEGER REFERENCES manufacturers                 (id)         , 
  statusId              INTEGER REFERENCES received_status               (id)         ,
  purpose               VARCHAR (30)                                              ,
  VVMTracked            BOOLEAN                                   DEFAULT TRUE   ,
  barCoded              BOOLEAN                                                       ,
  GS1                   BOOLEAN                                                       ,  
  quantity              INTEGER                                                       ,
  packsize              INTEGER                                                       ,
  unitPrice             NUMERIC(12,4)                                                 ,
  totalCost             NUMERIC(12,4)                                                 ,  
  locationId            INTEGER REFERENCES vaccine_storage               (id)         ,
  expectedDate          DATE                                                          ,
  arrivalDate           DATE                                                          ,
  confirmedBy           INTEGER                                                       ,
  note                  TEXT                                                          ,
  createdBy             INTEGER                                                       ,
  createdDate           TIMESTAMP                            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy            INTEGER                                                       ,
  modifiedDate          TIMESTAMP                            DEFAULT CURRENT_TIMESTAMP  
);
COMMENT ON TABLE inventory_transactions IS                    'Inventory transactions';
COMMENT ON COLUMN inventory_transactions.id IS                'ID';
COMMENT ON COLUMN inventory_transactions.transactionTypeId IS 'Transaction type';
COMMENT ON COLUMN inventory_transactions.fromFacilityId IS    'Received from';
COMMENT ON COLUMN inventory_transactions.toFacilityId IS      'Send to';
COMMENT ON COLUMN inventory_transactions.productId IS         'Product';
COMMENT ON COLUMN inventory_transactions.dispatchReference IS 'Dispatch reference';
COMMENT ON COLUMN inventory_transactions.dispatchDate IS      'Dispatch date';
COMMENT ON COLUMN inventory_transactions.bol IS               'Bill of lading';
COMMENT ON COLUMN inventory_transactions.donorId IS           'Donor';
COMMENT ON COLUMN inventory_transactions.originCountryId IS   'Country of origin';
COMMENT ON COLUMN inventory_transactions.manufacturerId IS    'Manufacturer';
COMMENT ON COLUMN inventory_transactions.statusId IS          'Received status';
COMMENT ON COLUMN inventory_transactions.purpose IS           'Purpose for the vaccine';
COMMENT ON COLUMN inventory_transactions.VVMTracked IS        'Consignment temperature monitored through VVM';
COMMENT ON COLUMN inventory_transactions.barCoded IS          'Consignment is bar coded';
COMMENT ON COLUMN inventory_transactions.GS1 IS               'GS1 bar coded';
COMMENT ON COLUMN inventory_transactions.quantity IS          'Quantity';
COMMENT ON COLUMN inventory_transactions.packsize IS          'Pack size';
COMMENT ON COLUMN inventory_transactions.unitPrice IS         'Unit price';
COMMENT ON COLUMN inventory_transactions.totalCost IS         'Total cost';
COMMENT ON COLUMN inventory_transactions.locationId IS        'Storage location ';
COMMENT ON COLUMN inventory_transactions.expectedDate IS      'Date the shipment expected';
COMMENT ON COLUMN inventory_transactions.arrivalDate IS       'Date the shipment arrived at destination';
COMMENT ON COLUMN inventory_transactions.confirmedBy IS       'Proof-of-receipt confirmed by';
COMMENT ON COLUMN inventory_transactions.note IS              'Notes';
COMMENT ON COLUMN inventory_transactions.createdBy IS         'Created by';
COMMENT ON COLUMN inventory_transactions.createdDate IS       'Created on';
COMMENT ON COLUMN inventory_transactions.modifiedBy IS        'Modified by';
COMMENT ON COLUMN inventory_transactions.modifiedDate IS      'Modified on';
-------------------------------------------------------------------
-- Table: inventory_batches
DROP TABLE IF EXISTS inventory_batches;
CREATE TABLE inventory_batches
(
  id                    SERIAL PRIMARY KEY                                            ,
  transactionId         INTEGER REFERENCES inventory_transactions         (id) NOT NULL,
  batchNumber           VARCHAR(250)                                          NOT NULL,
  manufactureDate       DATE                                                          ,
  expiryDate            DATE                                                  NOT NULL,
  quantity              INTEGER                                               NOT NULL,
  VVM1_qty              INTEGER                                                       ,
  VVM2_qty              INTEGER                                                       ,
  VVM3_qty              INTEGER                                                       ,
  VVM4_qty              INTEGER                                                       ,
  note                  VARCHAR(250)                                                  ,
  createdBy             INTEGER                                                       ,
  createdDate           TIMESTAMP                            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy            INTEGER                                                       ,
  modifiedDate          TIMESTAMP                            DEFAULT CURRENT_TIMESTAMP  
);
COMMENT ON TABLE inventory_batches IS                    'On hand of inventory';
COMMENT ON COLUMN inventory_batches.id IS                'ID';
COMMENT ON COLUMN inventory_batches.transactionId IS     'Inventory trasaction ID';
COMMENT ON COLUMN inventory_batches.batchNumber IS       'Batch/Lot number';
COMMENT ON COLUMN inventory_batches.manufactureDate IS   'Manufacturing date';
COMMENT ON COLUMN inventory_batches.expiryDate IS        'Expiry date';
COMMENT ON COLUMN inventory_batches.quantity IS          'Batch quantity';
COMMENT ON COLUMN inventory_batches.VVM1_qty IS          'VVM 1 quantity';
COMMENT ON COLUMN inventory_batches.VVM2_qty IS          'VVM 2 quantity';
COMMENT ON COLUMN inventory_batches.VVM3_qty IS          'VVM 3 quantity';
COMMENT ON COLUMN inventory_batches.VVM4_qty IS          'VVM 4 quantity';
COMMENT ON COLUMN inventory_batches.note IS              'Note';
COMMENT ON COLUMN inventory_batches.createdBy IS         'Created by';
COMMENT ON COLUMN inventory_batches.createdDate IS       'Created on';
COMMENT ON COLUMN inventory_batches.modifiedBy IS        'Modified by';
COMMENT ON COLUMN inventory_batches.modifiedDate IS      'Modified on';
-- Table: on_hand
DROP TABLE IF EXISTS on_hand;
CREATE TABLE on_hand
(
  id                SERIAL PRIMARY KEY                                        ,
  transactionId     INTEGER REFERENCES inventory_transactions    (id) NOT NULL,
  transactionTypeId INTEGER REFERENCES transaction_types         (id) NOT NULL,
  productId         INTEGER REFERENCES products                  (id) NOT NULL,
  facilityId        INTEGER REFERENCES facilities                (id) NOT NULL,
  batchNumber       INTEGER REFERENCES inventory_batches         (id) NOT NULL,
  quantity          INTEGER                                           NOT NULL,
  VVM1_qty          INTEGER                                                   ,
  VVM2_qty          INTEGER                                                   ,
  VVM3_qty          INTEGER                                                   ,
  VVM4_qty          INTEGER                                                   ,  
  note              VARCHAR (250)                                             ,
  createdBy         INTEGER                                                   ,
  createdDate       TIMESTAMP                        DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                                   ,
  modifiedDate      TIMESTAMP                        DEFAULT CURRENT_TIMESTAMP  
);
COMMENT ON TABLE on_hand IS                    'On hand of inventory';
COMMENT ON COLUMN on_hand.id IS                'ID';
COMMENT ON COLUMN on_hand.transactionId IS     'Trasaction reference';
COMMENT ON COLUMN on_hand.transactionTypeId IS 'Transaction Type';
COMMENT ON COLUMN on_hand.productId IS         'Product code';
COMMENT ON COLUMN on_hand.facilityId IS        'Facility ID';
COMMENT ON COLUMN on_hand.batchNumber IS       'Batch number';
COMMENT ON COLUMN on_hand.quantity IS          'Quantity';
COMMENT ON COLUMN on_hand.VVM1_qty IS          'VVM1';
COMMENT ON COLUMN on_hand.VVM2_qty IS          'VVM2';
COMMENT ON COLUMN on_hand.VVM3_qty IS          'VVM3';
COMMENT ON COLUMN on_hand.VVM4_qty IS          'VVM4';
COMMENT ON COLUMN on_hand.note IS              'Notes';
COMMENT ON COLUMN on_hand.createdBy IS         'Created by';
COMMENT ON COLUMN on_hand.createdDate IS       'Created on';
COMMENT ON COLUMN on_hand.modifiedBy IS        'Modified by';
COMMENT ON COLUMN on_hand.modifiedDate IS      'Modified on';


delete from countries where name='USA' or name ='France';
INSERT INTO countries( name)
    VALUES ('USA'),('France');
