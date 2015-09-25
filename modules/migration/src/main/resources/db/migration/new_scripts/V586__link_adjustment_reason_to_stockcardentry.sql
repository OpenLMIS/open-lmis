ALTER TABLE stock_card_entries
  RENAME COLUMN adjustmentreason TO adjustmenttype;
ALTER TABLE stock_card_entries
  ADD CONSTRAINT losses_adjustments_types_fkey
  FOREIGN KEY (adjustmenttype) REFERENCES losses_adjustments_types (name);