CREATE OR REPLACE FUNCTION cmm_at_day(facility_id INT, drugCode TEXT, day DATE)
  RETURNS DOUBLE PRECISION AS $$
DECLARE
  cmm DOUBLE PRECISION;
BEGIN
  cmm = (SELECT cmmvalue
         FROM cmm_entries
         WHERE cmm_entries.facilityId = facility_id AND
               productCode = drugCode AND
               periodBegin <= day AND
               periodEnd >= day);
  IF (cmm IS NULL)
  THEN
    RETURN -1;
  ELSE
    RETURN cmm;
  END IF;
END
$$
LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION soonest_expiry_date(expiryDatesString TEXT)
  RETURNS DATE AS $$
BEGIN
  RETURN (SELECT to_date(expireDates, 'DD-MM-YYYY') AS expireDate
          FROM unnest(string_to_array(expiryDatesString, ',')) AS expireDates
          ORDER BY expireDate ASC
          LIMIT 1);
END
$$
LANGUAGE 'plpgsql';

CREATE MATERIALIZED VIEW vw_daily_full_soh AS
  (SELECT DISTINCT ON (facility_code, drug_code, occurred)
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
     cmm_at_day(facilities.id, products.code, occurred)                       AS cmm
   FROM stock_card_entries
     JOIN stock_cards ON stock_card_entries.stockcardid = stock_cards.id
     JOIN products ON stock_cards.productid = products.id
     JOIN facilities ON stock_cards.facilityid = facilities.id
     JOIN geographic_zones AS ZONE ON facilities.geographiczoneid = ZONE.id
     JOIN geographic_zones AS parent_zone ON ZONE.parentid = parent_zone.id
     LEFT JOIN cmm_entries ON facilities.id = cmm_entries.facilityid
   ORDER BY facility_code, drug_code, occurred, stock_card_entries.createddate DESC);

CREATE OR REPLACE FUNCTION refresh_daily_full_soh()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW vw_daily_full_soh;
  RETURN 1;
END $$;