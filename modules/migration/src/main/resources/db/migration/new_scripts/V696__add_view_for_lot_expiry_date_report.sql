CREATE OR REPLACE VIEW vw_lot_expiry_dates AS

  SELECT
    lots.lotnumber      AS lot_number,
    lots.expirationdate AS expiration_date,
    stock_entry_loh.*
  FROM
    (SELECT
      facilities.name      AS facility_name,
      facilities.code      AS facility_code,
      zone.name            AS district_name,
      zone.code            AS district_code,
      parent_zone.name     AS province_name,
      parent_zone.code     AS province_code,
      products.code        AS drug_code,
      products.primaryname AS drug_name,
      (EXTRACT(EPOCH FROM stock_card_entries.createddate) * 1000) AS createddate,
      (EXTRACT(EPOCH FROM stock_card_entries.occurred) * 1000) AS occurred,
      stock_card_entry_key_values.keycolumn AS lot_id,
      NULLIF(stock_card_entry_key_values.valuecolumn, '')::int AS lot_on_hand
    FROM facilities
      JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
      JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
      JOIN stock_cards ON facilities.id = stock_cards.facilityid
      JOIN products ON stock_cards.productid = products.id
      JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
      JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
    WHERE stock_card_entry_key_values.keycolumn LIKE 'LOT#%'
    ORDER BY facility_code, drug_code, occurred, stock_card_entries.id DESC) stock_entry_loh
  JOIN lots
  ON stock_entry_loh.lot_id = ('LOT#' || lots.id);