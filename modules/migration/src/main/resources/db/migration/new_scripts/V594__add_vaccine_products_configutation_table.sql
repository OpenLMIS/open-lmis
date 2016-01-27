CREATE TABLE vaccine_inventory_product_configurations
(
  id serial NOT NULL,
  type character varying(50) NOT NULL,
  productid integer,
  batchtracked boolean,
  vvmtracked boolean,
  CONSTRAINT vaccine_inventory_config_pkey PRIMARY KEY (id),
  CONSTRAINT vaccine_inventory_config_product_fkey FOREIGN KEY (productid)
      REFERENCES products (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_inventory_product_configurations
  OWNER TO postgres;