DO $$
BEGIN
  DROP TABLE IF EXISTS stock_movement_lot_key_values;

  CREATE TABLE stock_movement_lot_key_values
  (
    stockmovementlotid integer NOT NULL,
    keycolumn text NOT NULL,
    valuecolumn text,
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now(),
    CONSTRAINT stock_movement_lot_key_values_pkey PRIMARY KEY (stockmovementlotid, keycolumn),
    CONSTRAINT stock_movement_lot_fkey FOREIGN KEY (stockmovementlotid)
    REFERENCES stock_movement_lots (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT stock_movement_lot_key_values_entry_key_key UNIQUE (stockmovementlotid, keycolumn)
  )
  WITH (
  OIDS=FALSE
  );
END;
$$