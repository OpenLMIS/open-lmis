CREATE OR REPLACE FUNCTION splitDates(datesStr TEXT)
  RETURNS SETOF DATE AS $$
DECLARE
  datesArray TEXT [];
  dateStr    TEXT;
BEGIN
  datesArray := regexp_split_to_array(datesStr, ',');
  FOREACH dateStr IN ARRAY datesArray
  LOOP
    RETURN NEXT to_date(dateStr, 'DD-MM-YYYY');
  END LOOP;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE VIEW vw_expiry_dates AS
  SELECT
    DISTINCT ON (facility_code, drug_code, expiry_date)
    facilities.name         AS facility_name,
    facilities.code         AS facility_code,
    zone.name               AS district_name,
    zone.code               AS district_code,
    parent_zone.name        AS province_name,
    parent_zone.code        AS province_code,
    products.code           AS drug_code,
    products.primaryname    AS drug_name,
    splitDates(valuecolumn) AS expiry_date
  FROM facilities
    JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
    JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
    JOIN stock_cards ON facilities.id = stock_cards.facilityid
    JOIN products ON stock_cards.productid = products.id
    JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
    JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
  WHERE keycolumn = 'expirationdates' AND valuecolumn != ''
  ORDER BY facility_code, drug_code;