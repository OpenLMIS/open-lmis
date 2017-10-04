CREATE TABLE treatments
(
  id serial NOT NULL,
  productcode character varying(50) NOT NULL,
  amount integer NOT NULL,
  stock integer NOT NULL,
  implementationid integer NOT NULL,
  CONSTRAINT treatments_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE treatments OWNER TO postgres;
ALTER TABLE treatments ADD CONSTRAINT
  product_code_fkey FOREIGN KEY (productcode)
  REFERENCES products (code) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
ALTER TABLE treatments ADD CONSTRAINT
  implementation_id_fkey FOREIGN KEY (implementationid)
  REFERENCES implementations (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
