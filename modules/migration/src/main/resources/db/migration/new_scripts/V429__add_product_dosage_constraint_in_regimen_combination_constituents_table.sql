ALTER TABLE regimen_combination_constituents
    DROP CONSTRAINT IF EXISTS  defalut_regimen_product_id_fkey;
ALTER TABLE regimen_combination_constituents
ADD  CONSTRAINT defalut_regimen_product_id_fkey FOREIGN KEY (default_dosage_id)
      REFERENCES regimen_constituents_dosages (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;