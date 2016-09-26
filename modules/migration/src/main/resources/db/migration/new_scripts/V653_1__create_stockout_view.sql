CREATE TYPE stock_out_event AS (duration INT, resolved_date DATE, is_resolved BOOLEAN);

CREATE OR REPLACE FUNCTION stockout_event(stockcard_id INTEGER, after_date DATE)
  RETURNS stock_out_event AS
$event$
DECLARE
  resolvedDate DATE;
  event        stock_out_event;
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
    SELECT
      (current_date - after_date),
      resolvedDate,
      FALSE
    INTO event;
  ELSE
    SELECT
      resolvedDate - after_date,
      resolvedDate,
      TRUE
    INTO event;
  END IF;
  RETURN event;
END;
$event$ LANGUAGE plpgsql;

CREATE OR REPLACE VIEW vw_stockouts AS
  SELECT
    facilities.name             AS facility_name,
    zone.name                   AS district,
    parent_zone.name            AS province,
    products.code               AS drug_code,
    products.primaryname        AS drug_name,
    programs.name               AS program,
    stock_card_entries.occurred AS stockout_date,
    (stockout_event(stock_cards.id, stock_card_entries.occurred)).*
  FROM facilities
    JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
    JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
    JOIN stock_cards ON facilities.id = stock_cards.facilityid
    JOIN products ON stock_cards.productid = products.id
    JOIN program_products ON products.id = program_products.productid
    JOIN programs ON program_products.programid = programs.id
    JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
    JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
  WHERE keycolumn = 'soh' AND valuecolumn = '0' AND stock_card_entries.quantity != 0
  ORDER BY facility_name, drug_name, stockout_date;