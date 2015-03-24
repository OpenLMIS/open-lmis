CREATE TABLE regimen_product_combinations
(
  id serial NOT NULL,
  regimen_id integer,
  name character varying(50),
  CONSTRAINT pk_regimen_product_combo_id PRIMARY KEY (id),
  CONSTRAINT fk_regimen_id FOREIGN KEY (regimen_id)
      REFERENCES regimens (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE regimen_product_combinations
  OWNER TO postgres;