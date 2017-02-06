DROP MATERIALIZED VIEW IF EXISTS vw_lot_daily_full_soh;

DROP MATERIALIZED VIEW IF EXISTS vw_lot_expiry_dates;

CREATE MATERIALIZED VIEW vw_lot_expiry_dates AS

  SELECT
    uuid_in(md5(random() :: TEXT || now() :: TEXT) :: cstring) AS uuid,

    lots.lotnumber      AS lot_number,
    lots.expirationdate AS expiration_date,
    stock_entry_loh.*
  FROM
    (SELECT
      stock_card_entries.id AS stock_card_entry_id,
      facilities.name      AS facility_name,
      facilities.code      AS facility_code,
      facilities.id        AS facility_id,
      zone.name            AS district_name,
      zone.code            AS district_code,
      parent_zone.name     AS province_name,
      parent_zone.code     AS province_code,
      products.code        AS drug_code,
      products.primaryname AS drug_name,
      (EXTRACT(EPOCH FROM stock_card_entries.createddate) * 1000) AS createddate,
      (EXTRACT(EPOCH FROM stock_card_entries.occurred) * 1000) AS occurred,
      stock_card_entries.occurred   AS occurred_date,
      stock_cards.modifieddate      AS last_sync_date,
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

CREATE UNIQUE INDEX idx_vw_lot_expiry_dates ON vw_lot_expiry_dates (stock_card_entry_id, uuid);

CREATE MATERIALIZED VIEW vw_lot_daily_full_soh AS
  (SELECT DISTINCT ON (facility_code, drug_code, occurred_date)
     vw_lot_expiry_dates.stock_card_entry_id                                    AS stock_card_entry_id,
     vw_lot_expiry_dates.facility_name                                          AS facility_name,
     vw_lot_expiry_dates.facility_code                                          AS facility_code,
     vw_lot_expiry_dates.facility_id                                            AS facility_id,

     vw_lot_expiry_dates.district_name                                          AS district_name,
     vw_lot_expiry_dates.district_code                                          AS district_code,

     vw_lot_expiry_dates.province_name                                          AS province_name,
     vw_lot_expiry_dates.province_code                                          AS province_code,

     vw_lot_expiry_dates.drug_code                                              AS drug_code,
     vw_lot_expiry_dates.drug_name                                              AS drug_name,

     set_value(vw_lot_expiry_dates.stock_card_entry_id, 'soh')                  AS soh,
     MIN(vw_lot_expiry_dates.expiration_date)                                   AS soonest_expiry_date,

     vw_lot_expiry_dates.occurred_date                                          AS occurred,
     vw_lot_expiry_dates.last_sync_date                                         AS last_sync_date,
     uuid_in(md5(random() :: TEXT || now() :: TEXT) :: cstring)                 AS uuid

   FROM vw_lot_expiry_dates
   WHERE vw_lot_expiry_dates.lot_on_hand > 0
   GROUP BY vw_lot_expiry_dates.facility_name, vw_lot_expiry_dates.facility_code,
      vw_lot_expiry_dates.district_name, vw_lot_expiry_dates.district_code,
      vw_lot_expiry_dates.province_name, vw_lot_expiry_dates.province_code,
      vw_lot_expiry_dates.drug_name, vw_lot_expiry_dates.drug_code,
      vw_lot_expiry_dates.facility_id, vw_lot_expiry_dates.last_sync_date,
      vw_lot_expiry_dates.stock_card_entry_id, vw_lot_expiry_dates.occurred_date
   ORDER BY facility_code, drug_code, occurred_date DESC);

CREATE UNIQUE INDEX idx_vw_lot_daily_full_soh ON vw_lot_daily_full_soh (uuid, stock_card_entry_id);