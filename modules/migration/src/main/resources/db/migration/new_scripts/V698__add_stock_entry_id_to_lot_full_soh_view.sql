DROP MATERIALIZED VIEW IF EXISTS vw_lot_daily_full_soh;

CREATE MATERIALIZED VIEW vw_lot_daily_full_soh AS
  (SELECT DISTINCT ON (facility_code, drug_code, occurred)
     stock_card_entries.id                                                    AS stock_card_entry_id,
     facilities.name                                                          AS facility_name,
     facilities.code                                                          AS facility_code,

     ZONE.name                                                                AS district_name,
     ZONE.code                                                                AS district_code,

     parent_zone.name                                                         AS province_name,
     parent_zone.code                                                         AS province_code,

     products.code                                                            AS drug_code,
     products.primaryname                                                     AS drug_name,

     set_value(stock_card_entries.id, 'soh')                                  AS soh,
     MIN(vw_lot_expiry_dates.expiration_date)                                 AS soonest_expiry_date,

     stock_card_entries.occurred,
     stock_cards.modifieddate                                                 AS last_sync_date,
     cmm_at_day(facilities.id, products.code, stock_card_entries.occurred)    AS cmm,
     uuid_in(md5(random() :: TEXT || now() :: TEXT) :: cstring)               AS uuid

   FROM stock_card_entries
     JOIN stock_cards ON stock_card_entries.stockcardid = stock_cards.id
     JOIN products ON stock_cards.productid = products.id
     JOIN facilities ON stock_cards.facilityid = facilities.id
     JOIN geographic_zones AS ZONE ON facilities.geographiczoneid = ZONE.id
     JOIN geographic_zones AS parent_zone ON ZONE.parentid = parent_zone.id
     LEFT JOIN cmm_entries ON facilities.id = cmm_entries.facilityid
     LEFT JOIN vw_lot_expiry_dates ON vw_lot_expiry_dates.stock_card_entry_id = stock_card_entries.id
      AND vw_lot_expiry_dates.lot_on_hand > 0
     GROUP BY facilities.name, facilities.code, ZONE.name, ZONE.code,
      parent_zone.name, parent_zone.code, products.primaryname, products.code,
      facilities.id, stock_cards.modifieddate, stock_card_entries.id
   ORDER BY facility_code, drug_code, occurred, stock_card_entries.createddate DESC);

CREATE UNIQUE INDEX idx_vw_lot_daily_full_soh ON vw_lot_daily_full_soh (uuid, facility_code, drug_code, last_sync_date);