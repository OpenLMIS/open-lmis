
DROP TABLE IF EXISTS price_schedule;
DROP TABLE IF EXISTS price_schedule_category;

CREATE TABLE price_schedule_category
(
  id                     SERIAL PRIMARY KEY                            ,
  price_category         VARCHAR (50)                          NOT NULL,
  createdBy              INTEGER                                       ,
  createdDate            TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy             INTEGER                                       ,
  modifiedDate           TIMESTAMP            DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE price_schedule_category IS                    'Price schedule category name';
COMMENT ON COLUMN price_schedule_category.id IS                'ID';
COMMENT ON COLUMN price_schedule_category.price_category IS    'Price category';

-- Seed with initial list
INSERT INTO price_schedule_category
(id , price_category,createdBy,modifiedBy)
VALUES
(1  , 'A', 1,1),
(2  , 'B',1,1),
(3  , 'C',1,1);


CREATE TABLE price_schedule
(
  id                     SERIAL PRIMARY KEY                                      ,
  priceCatid             INTEGER REFERENCES price_schedule_category (id) NOT NULL,
  productid              INTEGER REFERENCES products                (id) NOT NULL,
  sale_price             NUMERIC (12,4)                                          ,
  createdBy              INTEGER                                                 ,
  createdDate            TIMESTAMP                      DEFAULT CURRENT_TIMESTAMP,
  modifiedBy             INTEGER                                                 ,
  modifiedDate           TIMESTAMP                      DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE price_schedule IS                       'Price schedule. Set annually';
COMMENT ON COLUMN price_schedule.id IS                   'ID';;
COMMENT ON COLUMN price_schedule.sale_price IS           'Sale price';
COMMENT ON COLUMN price_schedule.createdBy IS            'Created by';
COMMENT ON COLUMN price_schedule.createdDate IS          'Created on';
COMMENT ON COLUMN price_schedule.modifiedBy IS           'Modified by';
COMMENT ON COLUMN price_schedule.modifiedDate IS         'Modified on';