-- Table: product_mapping
DROP TABLE IF EXISTS product_mapping;
CREATE TABLE product_mapping
(
	id serial primary key,
	productCode character varying(255) REFERENCES products (code) NOT NULL,
	manufacturerId integer REFERENCES manufacturers (id) NOT NULL,
	gtin character varying(255),
	elmis character varying(255),
	rhi character varying(255),
	ppmr character varying(255),
	who character varying(255),
	other1 character varying(255),
	other2 character varying(255),
	other3 character varying(255),
	other4 character varying(255),
	other5 character varying(255),
	createdBy integer,
	createdDate timestamp without time zone	DEFAULT now(),
	modifiedBy integer,
	modifiedDate  timestamp without time zone DEFAULT now()
);
ALTER TABLE product_mapping OWNER TO openlmis;
  COMMENT ON TABLE product_mapping IS  'product mapping';
  COMMENT ON COLUMN product_mapping.productCode IS  'productCode';
  COMMENT ON COLUMN product_mapping.gtin IS  'gtin';
  COMMENT ON COLUMN product_mapping.elmis IS  'elmis';
  COMMENT ON COLUMN product_mapping.rhi IS  'rhi';
  COMMENT ON COLUMN product_mapping.ppmr IS  'ppmr';
  COMMENT ON COLUMN product_mapping.who IS  'who';
  COMMENT ON COLUMN product_mapping.other1 IS  'other1';
  COMMENT ON COLUMN product_mapping.other2 IS  'other2';
  COMMENT ON COLUMN product_mapping.other3 IS  'other3';
  COMMENT ON COLUMN product_mapping.other4 IS  'other4';
  COMMENT ON COLUMN product_mapping.other5 IS  'other5';
