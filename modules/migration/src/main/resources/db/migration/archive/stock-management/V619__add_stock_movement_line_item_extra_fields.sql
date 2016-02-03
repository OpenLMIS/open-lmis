DROP TABLE IF EXISTS stock_movement_line_item_extra_fields;

CREATE TABLE stock_movement_line_item_extra_fields
(
  id serial NOT NULL,
  stockmovementlineitemid integer NOT NULL,
  issuevoucher character varying(250) NOT NULL,
  issuedate character varying(250) NOT NULL,
  tofacilityname character varying(250) NOT NULL,
  productid integer NOT NULL,
  dosesrequested integer NOT NULL,
  gap integer DEFAULT 0,
  productcategoryid integer,
  quantityonhand integer DEFAULT 0,
  createdby integer,
  createddate timestamp with time zone DEFAULT now(),
  CONSTRAINT stock_movement_line_item_fkey FOREIGN KEY (stockmovementlineitemid)
      REFERENCES stock_movement_line_items (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);