ALTER TABLE facilities DROP COLUMN IF EXISTS pricecatid;
DROP TABLE IF EXISTS price_schedule;
DROP TABLE IF EXISTS price_schedule_category;

CREATE TABLE price_schedules
(
  id                     SERIAL PRIMARY KEY ,
  code                   VARCHAR (50)  NOT NULL UNIQUE,
  description            VARCHAR (500) NULL,
  displayOrder           INT NOT NULL DEFAULT(0),
  createdBy              INTEGER  ,
  createdDate            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy             INTEGER  ,
  modifiedDate           TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_price_schedules
(
  id                     SERIAL PRIMARY KEY ,
  priceScheduleId        INTEGER REFERENCES price_schedules(id) NOT NULL ,
  productId              INTEGER REFERENCES products(id)NOT NULL ,
  price                  NUMERIC (12,4) NOT NULL DEFAULT(0),

  createdBy              INTEGER ,
  createdDate            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy             INTEGER ,
  modifiedDate           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  UNIQUE(priceScheduleId, productId)
);


ALTER TABLE facilities
  ADD COLUMN pricescheduleid INT NULL REFERENCES price_schedules(id);
