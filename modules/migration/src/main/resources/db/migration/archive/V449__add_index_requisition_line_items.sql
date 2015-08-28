-- Index: i_reported_figures

 DROP INDEX IF EXISTS i_reported_figures;

CREATE INDEX i_reported_figures
  ON requisition_line_items
  USING btree
  (((COALESCE(stockinhand, 0) + COALESCE(beginningbalance, 0) + COALESCE(quantitydispensed, 0) + COALESCE(quantityreceived, 0) + COALESCE(stockoutdays, 0) + abs(COALESCE(totallossesandadjustments, 0))) > 0));
COMMENT ON INDEX i_reported_figures
  IS ' used to manage data in data warehouse';

---------------------------------------

-- Index: i_requisition_line_items_modifieddate

DROP INDEX IF EXISTS i_requisition_line_items_modifieddate;

CREATE INDEX i_requisition_line_items_modifieddate
  ON requisition_line_items
  USING btree
  (modifieddate);
COMMENT ON INDEX i_requisition_line_items_modifieddate
  IS 'used to manage data in data warehouse';

----------------------------------------------
-- Index: i_requisition_line_items_skipped_f

DROP INDEX IF EXISTS i_requisition_line_items_skipped_f;

CREATE INDEX i_requisition_line_items_skipped_f
  ON requisition_line_items
  USING btree
  (rnrid)
  WHERE skipped = false;
COMMENT ON INDEX i_requisition_line_items_skipped_f
  IS 'used to manage data in data warehouse';  




