CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
DO
$do$
BEGIN
IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'm' and relname = 'vw_stockouts_tmp') THEN
CREATE MATERIALIZED VIEW vw_stockouts_tmp AS
  SELECT uuid_generate_v4() AS uuid,
    facilities.name AS facility_name,
    facilities.code AS facility_code,
    zone.name AS district_name,
    zone.code AS district_code,
    parent_zone.name AS province_name,
    parent_zone.code AS province_code,
    products.code AS drug_code,
    products.primaryname AS drug_name,
    programs.name AS program,
    stock_card_entries.occurred AS stockout_date,
    (calculate_each_month_duration(stock_cards.id, stock_card_entries.occurred, stock_card_entry_key_values.stockcardentryid)).resolved_date AS resolved_date,
    (calculate_each_month_duration(stock_cards.id, stock_card_entries.occurred, stock_card_entry_key_values.stockcardentryid)).overlapped_month AS overlapped_month,
    (calculate_each_month_duration(stock_cards.id, stock_card_entries.occurred, stock_card_entry_key_values.stockcardentryid)).overlap_duration AS overlap_duration,
    (calculate_each_month_duration(stock_cards.id, stock_card_entries.occurred, stock_card_entry_key_values.stockcardentryid)).is_resolved AS is_resolved
   FROM facilities
     JOIN geographic_zones zone ON facilities.geographiczoneid = zone.id
     JOIN geographic_zones parent_zone ON zone.parentid = parent_zone.id
     JOIN stock_cards ON facilities.id = stock_cards.facilityid
     JOIN products ON stock_cards.productid = products.id
     JOIN program_products ON products.id = program_products.productid
     JOIN programs ON program_products.programid = programs.id
     JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
     JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
  WHERE stock_card_entry_key_values.keycolumn = 'soh'::text AND stock_card_entry_key_values.valuecolumn = '0'::text AND stock_card_entries.quantity <> 0
  ORDER BY facilities.code, products.code, stock_card_entries.occurred, (calculate_each_month_duration(stock_cards.id, stock_card_entries.occurred, stock_card_entry_key_values.stockcardentryid)).overlapped_month, stock_card_entry_key_values.stockcardentryid;
END IF;
END
$do$;

DROP MATERIALIZED VIEW vw_stockouts;
ALTER MATERIALIZED VIEW vw_stockouts_tmp RENAME TO vw_stockouts;

