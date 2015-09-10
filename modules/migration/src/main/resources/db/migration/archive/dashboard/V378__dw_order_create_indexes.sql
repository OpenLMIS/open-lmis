DROP INDEX IF EXISTS dw_orders_index;
DROP INDEX IF EXISTS dw_orders_item_fill_rate;
DROP INDEX IF EXISTS dw_orders_lead_time;
DROP INDEX IF EXISTS dw_orders_order_fill_rate;
DROP INDEX IF EXISTS dw_orders_periodid;
DROP INDEX IF EXISTS i_dw_orders_geographiczoneid;
DROP INDEX IF EXISTS i_dw_orders_productid;
DROP INDEX IF EXISTS i_dw_orders_programid;
DROP INDEX IF EXISTS i_dw_orders_scheduleid;
DROP INDEX IF EXISTS i_dw_orders_stockedoutinpast;
DROP INDEX IF EXISTS indx_dw_orders_productcode;
DROP INDEX IF EXISTS indx_dw_orders_prog_period_rg_facility;
DROP INDEX IF EXISTS indx_dw_orders_rnrid;
DROP INDEX IF EXISTS indx_dw_orders_rnrid_periodid_productcode;
DROP INDEX IF EXISTS indx_dw_orders_rnrid_productcode;
DROP INDEX IF EXISTS indx_dw_orders_rnrid_status;
DROP INDEX IF EXISTS indx_dw_orders_status;


-- Index: dw_orders_index_facility
DROP INDEX IF EXISTS dw_orders_index_facility;

CREATE INDEX dw_orders_index_facility
ON dw_orders
USING btree
(facilityid);

-- Index: dw_orders_index_period

 DROP INDEX IF EXISTS dw_orders_index_period;

CREATE INDEX dw_orders_index_period
ON dw_orders
USING btree
(periodid);

-- Index: dw_orders_index_product

 DROP INDEX  IF EXISTS dw_orders_index_product;

CREATE INDEX dw_orders_index_product
ON dw_orders
USING btree
(productid);


-- Index: dw_orders_index_prog

 DROP INDEX  IF EXISTS dw_orders_index_prog;

CREATE INDEX dw_orders_index_prog
ON dw_orders
USING btree
(programid);

-- Index: dw_orders_index_schedule

 DROP INDEX  IF EXISTS dw_orders_index_schedule;

CREATE INDEX dw_orders_index_schedule
ON dw_orders
USING btree
(scheduleid);


-- Index: dw_orders_index_status

 DROP INDEX  IF EXISTS dw_orders_index_status;

CREATE INDEX dw_orders_index_status
ON dw_orders
USING btree
(status);

-- Index: dw_orders_index_zone

 DROP INDEX  IF EXISTS dw_orders_index_zone;

CREATE INDEX dw_orders_index_zone
ON dw_orders
USING btree
(geographiczoneid);

-- Index: i_dw_orders_stockedoutinpast
DROP INDEX  IF EXISTS i_dw_orders_stockedoutinpast;

CREATE INDEX i_dw_orders_stockedoutinpast
  ON dw_orders
  USING btree
  (stockedoutinpast);
