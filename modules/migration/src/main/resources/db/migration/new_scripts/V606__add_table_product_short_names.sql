

 DROP TABLE IF EXISTS product_short_names;
CREATE TABLE product_short_names
(
  id serial PRIMARY KEY,
  productid integer,
  name character varying(50) NOT NULL,
  createdby integer,
  createddate timestamp with time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp with time zone DEFAULT now(),
  CONSTRAINT product_short_names_productId_fkey FOREIGN KEY (productid)
      REFERENCES products (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE product_short_names
  OWNER TO postgres;