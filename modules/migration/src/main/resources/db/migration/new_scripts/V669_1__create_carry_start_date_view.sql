CREATE OR REPLACE FUNCTION first_movement_date(cardid INTEGER)
  RETURNS DATE AS
$BODY$
BEGIN
  RETURN (SELECT occurred
          FROM stock_card_entries
          WHERE stockcardid = cardid
          ORDER BY occurred
          LIMIT 1);
END
$BODY$
LANGUAGE 'plpgsql';

CREATE MATERIALIZED VIEW vw_carry_start_dates AS
  SELECT
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

CREATE OR REPLACE FUNCTION refresh_start_carry_view()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW vw_carry_start_dates;
  RETURN 1;
END $$;