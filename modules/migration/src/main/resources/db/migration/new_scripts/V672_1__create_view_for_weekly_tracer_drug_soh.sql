CREATE TYPE stock_history AS ( facility_name TEXT, drug_name TEXT, date DATE, soh TEXT,
                               facility_code TEXT, drug_code TEXT, province_name TEXT, province_code TEXT, district_name TEXT, district_code TEXT);

CREATE OR REPLACE FUNCTION soh_of_day(cardid INTEGER, day DATE)
  RETURNS TEXT AS $BODY$
DECLARE
  soh TEXT;
BEGIN
  SELECT valuecolumn
  FROM stock_card_entry_key_values
    JOIN stock_card_entries ON stock_card_entry_key_values.stockcardentryid = stock_card_entries.id
  WHERE keycolumn = 'soh' AND occurred <= day AND stock_card_entries.stockcardid = cardid
  ORDER BY stockcardentryid DESC
  LIMIT 1
  INTO soh;

  RETURN soh;
END
$BODY$
LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION tracer_drugs_weekly_stock_history()
  RETURNS SETOF stock_history AS
$BODY$
DECLARE
  first_movement_date       DATE;
  last_movement_date        DATE;
  one_friday                DATE;
  one_line                  stock_history;
  tracer_drug_stockcard_ids INTEGER ARRAY;
BEGIN

  tracer_drug_stockcard_ids=array(SELECT id AS stockcardid
                                  FROM stock_cards
                                  WHERE productid IN (SELECT id
                                                      FROM products
                                                      WHERE tracer = TRUE));

  SELECT occurred
  FROM stock_card_entries
  ORDER BY occurred
  LIMIT 1
  INTO first_movement_date;

  SELECT occurred
  FROM stock_card_entries
  ORDER BY occurred DESC
  LIMIT 1
  INTO last_movement_date;

  FOR one_friday IN (SELECT *
                     FROM generate_series(first_movement_date :: DATE, last_movement_date, '1 day')
                     WHERE EXTRACT(DOW FROM generate_series) = 5)
  LOOP
    FOR one_line IN (SELECT
                       facilities.name                             AS facility_name,
                       products.primaryname                        AS drug_name,
                       one_friday                                  AS date,
                       soh_of_day(entries.stockcardid, one_friday) AS soh,

                       facilities.code                             AS facility_code,
                       products.code                               AS drug_code,
                       parent_zone.name                            AS province_name,
                       parent_zone.code                            AS province_code,
                       ZONE.name                                   AS district_name,
                       ZONE.code                                   AS district_code

                     FROM (SELECT DISTINCT ON (stockcardid) stockcardid
                           FROM stock_card_entries
                           WHERE stockcardid = ANY (tracer_drug_stockcard_ids) AND occurred <= one_friday) entries
                       JOIN stock_cards ON entries.stockcardid = stock_cards.id
                       JOIN facilities ON stock_cards.facilityid = facilities.id
                       JOIN products ON stock_cards.productid = products.id
                       JOIN geographic_zones AS ZONE ON facilities.geographiczoneid = ZONE.id
                       JOIN geographic_zones AS parent_zone ON ZONE.parentid = parent_zone.id)
    LOOP
      RETURN NEXT one_line;
    END LOOP;
  END LOOP;
END
$BODY$
LANGUAGE 'plpgsql';

CREATE MATERIALIZED VIEW vw_weekly_tracer_soh AS (SELECT *
                                                  FROM tracer_drugs_weekly_stock_history());

CREATE OR REPLACE FUNCTION refresh_weekly_tracer_soh()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW vw_weekly_tracer_soh;
  RETURN 1;
END $$;