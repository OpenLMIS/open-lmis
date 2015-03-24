CREATE TABLE regimen_combination_constituents
(
  id SERIAL   NOT NULL,
  default_dosage_id integer,
  product_combo_id integer,
  product_id integer,
  CONSTRAINT regimen_constituents_pkey PRIMARY KEY (id),
  CONSTRAINT fk_combo_id FOREIGN KEY (product_combo_id)
      REFERENCES regimen_product_combinations (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE regimen_combination_constituents
  OWNER TO postgres;
