CREATE OR REPLACE FUNCTION fn_stockout_days(stockcard_id INTEGER, after_date DATE)
  RETURNS INT AS
$days$
DECLARE
  resolvedDate DATE;
BEGIN

  SELECT occurred
  FROM stock_card_entry_key_values
    JOIN stock_card_entries ON stock_card_entry_key_values.stockcardentryid = stock_card_entries.id
  WHERE stock_card_entries.stockcardid = stockcard_id AND
        keycolumn = 'soh' AND
        valuecolumn != '0' AND
        stock_card_entries.occurred > after_date
  ORDER BY occurred ASC
  LIMIT 1
  INTO resolvedDate;
  IF resolvedDate IS NULL
  THEN
    RETURN current_date - after_date;
  ELSE
    RETURN resolvedDate - after_date;
  END IF;
END;
$days$ LANGUAGE plpgsql;

CREATE OR REPLACE VIEW vw_stockouts AS
  SELECT
    facilities.name                                               AS facility_name,
    products.code                                                 AS drug_code,
    products.primaryname                                          AS drug_name,
    stock_card_entries.occurred                                   AS stockout_date,
    fn_stockout_days(stock_cards.id, stock_card_entries.occurred) AS stockout_days
  FROM facilities
    JOIN stock_cards ON facilities.id = stock_cards.facilityid
    JOIN products ON stock_cards.productid = products.id
    JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
    JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
  WHERE keycolumn = 'soh' AND valuecolumn = '0'
  ORDER BY facility_name, drug_name, stockout_date;
