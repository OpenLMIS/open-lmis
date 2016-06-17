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

CREATE OR REPLACE FUNCTION records_group_by_reason(cardid INTEGER, periodStart TIMESTAMP, periodEnd TIMESTAMP)
  RETURNS TABLE(reason_code TEXT, occurrences BIGINT, total_quantity BIGINT) AS $BODY$
BEGIN
  RETURN QUERY (SELECT
                  adjustment_types.name :: TEXT AS reason_code,
                  count(entries.adjustmenttype) AS occurrences,
                  abs(sum(entries.quantity))    AS total_quantity
                FROM stock_card_entries entries
                  JOIN losses_adjustments_types adjustment_types
                    ON entries.adjustmenttype = adjustment_types.name
                WHERE entries.stockcardid = cardid AND entries.occurred >= periodStart AND entries.occurred <= periodEnd
                GROUP BY reason_code
  );
END
$BODY$
LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION total_quantity_and_occurrences(cardid INTEGER, periodStart TIMESTAMP, periodEnd TIMESTAMP)
  RETURNS TABLE(reason_code TEXT, occurrences BIGINT, total_quantity BIGINT) AS $BODY$
DECLARE
  found           BOOLEAN;
  has_consumption BOOLEAN;
BEGIN

  SELECT exists(SELECT adjustmenttype
                FROM stock_card_entries
                WHERE stockcardid = cardid AND occurred >= periodStart AND occurred <= periodEnd)
  INTO found;

  SELECT exists(SELECT
                  adjustmenttype,
                  category
                FROM stock_card_entries
                  JOIN losses_adjustments_types
                    ON stock_card_entries.adjustmenttype = losses_adjustments_types.name
                WHERE category = 'ISSUE' AND stockcardid = cardid AND occurred >= periodStart AND occurred <= periodEnd)
  INTO has_consumption;

  IF (found)
  THEN
    IF (has_consumption)
    THEN
      RETURN QUERY (SELECT *
                    FROM records_group_by_reason(cardid, periodStart, periodEnd));
    ELSE
      RETURN QUERY (SELECT *
                    FROM records_group_by_reason(cardid, periodStart, periodEnd)
                    UNION
                    SELECT
                      'ISSUE' :: TEXT,
                      0 :: BIGINT,
                      0 :: BIGINT);
    END IF;
  ELSE
    RETURN QUERY (SELECT
                    'NO_MOVEMENT_IN_PERIOD' :: TEXT,
                    0 :: BIGINT,
                    0 :: BIGINT);
  END IF;
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
               AND cmm_entries.periodbegin = periodStart :: DATE
               AND cmm_entries.periodend = periodEnd_P :: DATE);

  IF (cmm = -1)
  THEN
    RETURN NULL;
  ELSE
    RETURN cmm;
  END IF;

END
$$
LANGUAGE 'plpgsql';

DROP MATERIALIZED VIEW vw_period_movements;

CREATE MATERIALIZED VIEW vw_period_movements AS
  (SELECT
     uuid_in(md5(random() :: TEXT || now() :: TEXT) :: cstring) AS uuid,

     periodStart,
     periodEnd,

     facilities.name                                            AS facility_name,
     products.primaryname                                       AS drug_name,
     facilities.code                                            AS facility_code,
     products.code                                              AS drug_code,
     parent_zone.name                                           AS province_name,
     parent_zone.code                                           AS province_code,
     ZONE.name                                                  AS district_name,
     ZONE.code                                                  AS district_code,

     soh_of_day(stockcardid, periodEnd :: DATE) :: INTEGER      AS soh,
     cmm_of(stockcardid, periodStart, periodEnd)                AS cmm,

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

CREATE UNIQUE INDEX idx_vw_period_movements ON vw_period_movements (uuid);