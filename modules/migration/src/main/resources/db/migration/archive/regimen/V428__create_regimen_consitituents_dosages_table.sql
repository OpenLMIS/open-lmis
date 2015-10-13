DROP TABLE IF EXISTS regimen_constituents_dosages;
CREATE TABLE regimen_constituents_dosages
(
  id SERIAL   NOT NULL,
  regimenproductid integer NOT NULL,
  quantity numeric,
  dosageunitid integer,
  dosagefrequencyid integer,
  CONSTRAINT egimen_product_dosage_pkey PRIMARY KEY (id),
  CONSTRAINT regimens_product_dosage_fkey FOREIGN KEY (regimenproductid)
      REFERENCES regimen_combination_constituents (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT dosage_frequency_id_fkey FOREIGN KEY (dosagefrequencyid)
      REFERENCES dosage_frequencies (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT dosage_unit_id_fkey FOREIGN KEY (dosageunitid)
      REFERENCES dosage_units (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE regimen_constituents_dosages
  OWNER TO postgres;