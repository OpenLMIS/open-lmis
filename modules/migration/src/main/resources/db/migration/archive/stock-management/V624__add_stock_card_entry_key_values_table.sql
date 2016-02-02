DO $$
BEGIN
  DROP TABLE IF EXISTS stock_card_entry_key_values;

  CREATE TABLE stock_card_entry_key_values
  (
    stockcardentryid integer NOT NULL,
    keycolumn text NOT NULL,
    valuecolumn text,
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now(),
    CONSTRAINT stock_card_entry_key_values_pkey PRIMARY KEY (stockcardentryid, keycolumn),
    CONSTRAINT stock_card_entry_fkey FOREIGN KEY (stockcardentryid)
    REFERENCES stock_card_entries (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT stock_card_entry_key_values_entry_key_key UNIQUE (stockcardentryid, keycolumn)
  )
  WITH (
  OIDS=FALSE
  );

  ALTER TABLE stock_card_entry_key_values
  OWNER TO postgres;

END;
$$