DO $$
BEGIN
  DROP TABLE IF EXISTS stock_card_entry_lot_items;

  CREATE TABLE stock_card_entry_lot_items
  (
    id serial NOT NULL,
    stockcardentryid integer NOT NULL,
    lotid integer NOT NULL,
    quantity integer DEFAULT 0,
    effectivedate timestamp with time zone DEFAULT now(),
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now(),
    CONSTRAINT stock_card_entry_lot_items_pkey PRIMARY KEY (id),
    CONSTRAINT stock_card_entry_fkey FOREIGN KEY (stockcardentryid)
        REFERENCES stock_card_entries (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT lot_fkey FOREIGN KEY (lotid)
        REFERENCES lots (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
  )
  WITH (
  OIDS=FALSE
  );

  DROP TABLE IF EXISTS stock_card_entry_lot_items_key_values;

  CREATE TABLE stock_card_entry_lot_items_key_values
  (
    stockcardentrylotitemid integer NOT NULL,
    keycolumn text NOT NULL,
    valuecolumn text,
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now(),
    CONSTRAINT stock_card_entry_lot_items_key_values_pkey PRIMARY KEY (stockcardentrylotitemid, keycolumn),
    CONSTRAINT stock_card_entry_lot_item_fkey FOREIGN KEY (stockcardentrylotitemid)
    REFERENCES stock_card_entry_lot_items (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT stock_card_entry_lot_items_key_values_entry_key_key UNIQUE (stockcardentrylotitemid, keycolumn)
  )
  WITH (
  OIDS=FALSE
  );
END;
$$