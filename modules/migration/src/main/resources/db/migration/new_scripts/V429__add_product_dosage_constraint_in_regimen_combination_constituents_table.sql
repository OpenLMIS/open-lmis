ALTER TABLE regimen_combination_constituents
ADD  CONSTRAINT fk_defalut_regimen_product_id FOREIGN KEY (default_dosage_id)
      REFERENCES regimen_constituents_dosages (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;