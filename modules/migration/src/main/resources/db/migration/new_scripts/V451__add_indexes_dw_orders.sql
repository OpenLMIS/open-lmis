DROP INDEX  IF EXISTS dw_orders_date_idx;
DROP INDEX IF EXISTS i_dw_orders_modifieddate;

CREATE INDEX i_dw_orders_modifieddate
  ON dw_orders
  USING btree
  (modifieddate);