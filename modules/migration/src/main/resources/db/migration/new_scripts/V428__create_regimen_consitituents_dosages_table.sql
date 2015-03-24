CREATE TABLE regimen_constituents_dosages
(
  id SERIAL   NOT NULL,
  regimen_product_id integer NOT NULL,
  quantity numeric,
  dosage_unit_id integer,
  dosage_frequency_id integer,
  CONSTRAINT egimen_product_dosage_pkey PRIMARY KEY (id),
  CONSTRAINT regimens_product_dosage_fkey FOREIGN KEY (regimen_product_id)
      REFERENCES regimen_combination_constituents (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_dosage_frequency_id FOREIGN KEY (dosage_frequency_id)
      REFERENCES dosage_frequencies (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_dosage_unit_id FOREIGN KEY (dosage_unit_id)
      REFERENCES dosage_units (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE regimen_constituents_dosages
  OWNER TO postgres;