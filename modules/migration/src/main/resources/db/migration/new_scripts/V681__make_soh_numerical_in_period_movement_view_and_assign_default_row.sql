CREATE OR REPLACE FUNCTION records_group_by_reason(cardid INTEGER, periodStart TIMESTAMP, periodEnd TIMESTAMP)
  RETURNS TABLE(reason_code TEXT, occurrences BIGINT, total_quantity BIGINT) AS $BODY$
BEGIN
  RETURN QUERY (SELECT *
                FROM (SELECT
                        (SELECT CASE
                                WHEN adjustmenttype IN ('PUB_PHARMACY',
                                                        'MATERNITY',
                                                        'GENERAL_WARD',
                                                        'ACC_EMERGENCY',
                                                        'LABORATORY',
                                                        'UATS',
                                                        'PNCTL',
                                                        'PAV',
                                                        'DENTAL_WARD',
                                                        'UNPACK_KIT')
                                  THEN 'CONSUMPTION'
                                WHEN adjustmenttype IN ('EXPIRED_RETURN_TO_SUPPLIER',
                                                        'DAMAGED',
                                                        'LOANS_DEPOSIT',
                                                        'INVENTORY_NEGATIVE',
                                                        'PROD_DEFECTIVE')
                                  THEN 'NEGATIVE'
                                WHEN adjustmenttype IN ('CUSTOMER_RETURN',
                                                        'EXPIRED_RETURN_FROM_CUSTOMER',
                                                        'DONATION',
                                                        'LOANS_RECEIVED',
                                                        'INVENTORY_POSITIVE',
                                                        'RETURN_FROM_QUARANTINE')
                                  THEN 'POSITIVE'
                                ELSE 'OTHER'
                                END AS reason) :: TEXT AS reason_code,
                        count(adjustmenttype)          AS occurrences,
                        abs(sum(quantity))             AS total_quantity
                      FROM stock_card_entries
                      WHERE stockcardid = cardid AND occurred >= periodStart AND occurred <= periodEnd
                      GROUP BY reason_code) AS X
                WHERE X.reason_code != 'OTHER');
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

  SELECT exists(SELECT adjustmenttype
                FROM stock_card_entries
                WHERE adjustmenttype IN ('PUB_PHARMACY',
                                         'MATERNITY',
                                         'GENERAL_WARD',
                                         'ACC_EMERGENCY',
                                         'LABORATORY',
                                         'UATS',
                                         'PNCTL',
                                         'PAV',
                                         'DENTAL_WARD',
                                         'UNPACK_KIT') AND stockcardid = cardid AND occurred >= periodStart AND
                      occurred <= periodEnd)
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
                      'CONSUMPTION' :: TEXT,
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

CREATE OR REPLACE FUNCTION refresh_period_movements()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW CONCURRENTLY vw_period_movements;
  RETURN 1;
END $$;