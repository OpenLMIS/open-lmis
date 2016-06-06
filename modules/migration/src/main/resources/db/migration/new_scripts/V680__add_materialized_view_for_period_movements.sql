-- For each period, each drug in each facility will have N rows in this view
-- N is determined by how many different movement reasons were used
-- Each row should have information representing:
-- location(province and district), HF, Drug, Period(start, end), movement reason,
-- sum of movement quantify for this reason, times of movements for this reason, SOH, CMM

CREATE OR REPLACE FUNCTION existing_card_ids_in_period(periodEnd TIMESTAMP)
  RETURNS SETOF INTEGER AS $BODY$
BEGIN
  RETURN QUERY (SELECT DISTINCT stock_cards.id
                FROM stock_cards
                  JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
                WHERE occurred <= periodEnd);
END
$BODY$
LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION total_quantity_and_occurrences(cardid INTEGER, periodStart TIMESTAMP, periodEnd TIMESTAMP)
  RETURNS TABLE(reason_code TEXT, occurrences BIGINT, total_quantity BIGINT) AS $BODY$
BEGIN
  RETURN QUERY (SELECT
                  adjustmenttype,
                  count(adjustmenttype),
                  abs(sum(quantity))
                FROM stock_card_entries
                WHERE stockcardid = cardid AND occurred >= periodStart AND occurred <= periodEnd
                GROUP BY adjustmenttype);
END
$BODY$
LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION cmm_of(cardid INTEGER, periodStart TIMESTAMP, periodEnd_P TIMESTAMP)
  RETURNS DOUBLE PRECISION AS $$
DECLARE
  cmm DOUBLE PRECISION;
BEGIN
  cmm = (SELECT cmmvalue
         FROM cmm_entries
         WHERE facilityid = (SELECT facilities.id
                             FROM facilities
                               JOIN stock_cards ON facilities.id = stock_cards.facilityid
                             WHERE stock_cards.id = cardid)
               AND
               productcode = (SELECT code
                              FROM products
                              WHERE id = (SELECT productid
                                          FROM stock_cards
                                          WHERE id = cardid))
               AND cmm_entries.periodbegin = periodStart
               AND cmm_entries.periodend = periodEnd_P);

  IF (cmm IS NULL)
  THEN
    RETURN -1;
  ELSE
    RETURN cmm;
  END IF;

END
$$
LANGUAGE 'plpgsql';

CREATE MATERIALIZED VIEW vw_period_movements AS
  (SELECT
     periodStart,
     periodEnd,

     facilities.name                             AS facility_name,
     products.primaryname                        AS drug_name,
     facilities.code                             AS facility_code,
     products.code                               AS drug_code,
     parent_zone.name                            AS province_name,
     parent_zone.code                            AS province_code,
     ZONE.name                                   AS district_name,
     ZONE.code                                   AS district_code,

     soh_of_day(stockcardid, periodEnd :: DATE)  AS soh,
     cmm_of(stockcardid, periodStart, periodEnd) AS cmm,

     (total_quantity_and_occurrences(stockcardid, periodStart,
                                     periodEnd)).*

   FROM (SELECT
           startdate                            AS periodStart,
           enddate                              AS periodEnd,
           existing_card_ids_in_period(enddate) AS stockcardid
         FROM processing_periods) AS cardIdsInPeriods
     JOIN stock_cards ON cardIdsInPeriods.stockcardid = stock_cards.id
     JOIN facilities ON stock_cards.facilityid = facilities.id
     JOIN products ON stock_cards.productid = products.id
     JOIN geographic_zones AS ZONE
       ON facilities.geographiczoneid = ZONE.id
     JOIN geographic_zones AS parent_zone
       ON ZONE.parentid = parent_zone.id);


CREATE OR REPLACE FUNCTION refresh_period_movements()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW CONCURRENTLY vw_period_movements;
  RETURN 1;
END $$;