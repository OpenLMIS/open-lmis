DROP MATERIALIZED VIEW IF EXISTS vw_stockouts;

CREATE MATERIALIZED VIEW vw_stockouts AS
  SELECT
    uuid_in(md5(random() :: TEXT || now() :: TEXT) :: cstring) AS uuid,

    facilities.name             AS facility_name,
    facilities.code             AS facility_code,
    zone.name                   AS district_name,
    zone.code                   AS district_code,
    parent_zone.name            AS province_name,
    parent_zone.code            AS province_code,
    products.code               AS drug_code,
    products.primaryname        AS drug_name,
    programs.name               AS program,
    stock_card_entries.occurred AS stockout_date,
    (calculate_each_month_duration(stock_cards.id, stock_card_entries.occurred, stockcardentryid)).*
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
  ORDER BY facility_code, drug_code, stockout_date, overlapped_month, stockcardentryid;

CREATE UNIQUE INDEX idx_vw_stockouts ON vw_stockouts (uuid);

DROP MATERIALIZED VIEW vw_carry_start_dates;

CREATE MATERIALIZED VIEW vw_carry_start_dates AS
  SELECT
    uuid_in(md5(random() :: TEXT || now() :: TEXT) :: cstring) AS uuid,

    facilities.name                     AS facility_name,
    facilities.code                     AS facility_code,
    ZONE.name                           AS district_name,
    ZONE.code                           AS district_code,
    parent_zone.name                    AS province_name,
    parent_zone.code                    AS province_code,
    products.code                       AS drug_code,
    products.primaryname                AS drug_name,
    facilities.golivedate               AS facility_golive_date,
    facilities.godowndate               AS facility_godown_date,
    first_movement_date(stock_cards.id) AS carry_start_date
  FROM stock_cards
    JOIN facilities ON stock_cards.facilityid = facilities.id
    JOIN products ON stock_cards.productid = products.id
    JOIN geographic_zones AS ZONE ON facilities.geographiczoneid = ZONE.id
    JOIN geographic_zones AS parent_zone ON ZONE.parentid = parent_zone.id
  ORDER BY facility_code, carry_start_date;

CREATE UNIQUE INDEX idx_vw_carry_start_dates ON vw_carry_start_dates (uuid);

DROP MATERIALIZED VIEW IF EXISTS vw_weekly_tracer_soh;

CREATE MATERIALIZED VIEW vw_weekly_tracer_soh AS
(SELECT
 uuid_in(md5(random() :: TEXT || now() :: TEXT) :: cstring) AS uuid,
 *
 FROM tracer_drugs_weekly_stock_history());

CREATE UNIQUE INDEX idx_vw_weekly_tracer_soh ON vw_weekly_tracer_soh (uuid);

DROP MATERIALIZED VIEW IF EXISTS vw_daily_full_soh;

CREATE MATERIALIZED VIEW vw_daily_full_soh AS
  (SELECT
     DISTINCT ON (facility_code, drug_code, occurred)
     facilities.name                                                          AS facility_name,
     facilities.code                                                          AS facility_code,

     ZONE.name                                                                AS district_name,
     ZONE.code                                                                AS district_code,

     parent_zone.name                                                         AS province_name,
     parent_zone.code                                                         AS province_code,

     products.code                                                            AS drug_code,
     products.primaryname                                                     AS drug_name,

     set_value(stock_card_entries.id, 'soh')                                  AS soh,
     soonest_expiry_date(set_value(stock_card_entries.id, 'expirationdates')) AS soonest_expiry_date,

     occurred,
     stock_cards.modifieddate                                                 AS last_sync_date,
     cmm_at_day(facilities.id, products.code, occurred)                       AS cmm,

     uuid_in(md5(random() :: TEXT || now() :: TEXT) :: cstring)               AS uuid

   FROM stock_card_entries
     JOIN stock_cards ON stock_card_entries.stockcardid = stock_cards.id
     JOIN products ON stock_cards.productid = products.id
     JOIN facilities ON stock_cards.facilityid = facilities.id
     JOIN geographic_zones AS ZONE ON facilities.geographiczoneid = ZONE.id
     JOIN geographic_zones AS parent_zone ON ZONE.parentid = parent_zone.id
     LEFT JOIN cmm_entries ON facilities.id = cmm_entries.facilityid
   ORDER BY facility_code, drug_code, occurred, stock_card_entries.createddate DESC);
