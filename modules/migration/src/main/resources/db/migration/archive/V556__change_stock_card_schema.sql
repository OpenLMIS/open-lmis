DO $$
  BEGIN
    ALTER TYPE stockcardlineitemtype RENAME TO stockcardlineitemtype_old;
    CREATE TYPE stockcardlineitemtype AS ENUM('DEBIT', 'CREDIT', 'ADJUSTMENT');
    ALTER TABLE stock_card_line_items
      ALTER COLUMN type TYPE stockcardlineitemtype USING type::text::stockcardlineitemtype;
    DROP TYPE stockcardlineitemtype_old;
  END;
$$