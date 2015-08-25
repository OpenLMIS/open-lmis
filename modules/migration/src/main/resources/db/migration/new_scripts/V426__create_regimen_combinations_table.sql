DROP TABLE IF EXISTS regimen_product_combinations;
CREATE TABLE regimen_product_combinations
(
  id serial NOT NULL,
  regimenid integer,
  name character varying(50),
  CONSTRAINT regimen_product_combo_id_pkey PRIMARY KEY (id),
  CONSTRAINT regimen_id_fkey FOREIGN KEY (regimenid)
      REFERENCES regimens (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE regimen_product_combinations
  OWNER TO postgres;