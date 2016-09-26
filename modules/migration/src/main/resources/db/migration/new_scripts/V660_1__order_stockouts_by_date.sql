CREATE OR REPLACE VIEW vw_stockouts AS
  SELECT
    facilities.name             AS facility_name,
    zone.name                   AS district,
    parent_zone.name            AS province,
    products.code               AS drug_code,
    products.primaryname        AS drug_name,
    programs.name               AS program,
    stock_card_entries.occurred AS stockout_date,
    (stockout_event(stock_cards.id, stock_card_entries.occurred, stock_card_entry_key_values.createddate)).*
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
  ORDER BY stockout_date;