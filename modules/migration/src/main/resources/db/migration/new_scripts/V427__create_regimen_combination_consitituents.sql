DROP TABLE IF EXISTS regimen_combination_constituents;
CREATE TABLE regimen_combination_constituents
(
  id SERIAL   NOT NULL,
  defaultdosageid integer,
  productcomboid integer,
  productid integer,
  CONSTRAINT regimen_constituents_pkey PRIMARY KEY (id),
  CONSTRAINT combo_id_fkey FOREIGN KEY (productcomboid)
      REFERENCES regimen_product_combinations (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE regimen_combination_constituents
  OWNER TO postgres;
