CREATE OR REPLACE FUNCTION records_group_by_reason(cardid INTEGER, periodStart TIMESTAMP, periodEnd TIMESTAMP)
  RETURNS TABLE(reason_code TEXT, occurrences BIGINT, total_quantity BIGINT) AS $BODY$
BEGIN
  RETURN QUERY (SELECT adjustment_types.name :: TEXT          AS reason_code,
                  count(entries.adjustmenttype)           AS occurrences,
                  abs(sum(entries.quantity))              AS total_quantity
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

  SELECT exists(SELECT adjustmenttype, category
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




