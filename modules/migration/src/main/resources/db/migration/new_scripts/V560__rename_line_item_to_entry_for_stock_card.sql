DO $$
BEGIN
  ALTER TYPE stockcardlineitemtype RENAME TO stockcardentrytype;

  ALTER TABLE stock_card_line_items RENAME TO stock_card_entries;
END;
$$